package org.example.rootmanage.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.module.ModuleEntity;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.permission.RoleModulePermission;
import org.example.rootmanage.role.Role;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 权限验证拦截器
 * 根据用户的角色和模块权限，验证是否有权限执行当前操作
 */
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final PermissionService permissionService;
    private final ModuleRepository moduleRepository;

    // 不需要权限验证的路径
    private static final String[] EXCLUDED_PATHS = {
            "/api/auth/login",
            "/api/register",
            "/api/register/check",
            "/api/options"  // 注册页面需要访问选项接口
    };

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 允许OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 排除不需要权限验证的路径（精确匹配或前缀匹配）
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.equals(excludedPath) || path.startsWith(excludedPath + "/")) {
                System.out.println("[权限拦截] 排除路径，允许访问: " + path);
                return true;
            }
        }

        // 只验证 /api 开头的路径
        if (!path.startsWith("/api")) {
            return true;
        }

        try {
            System.out.println("[权限拦截] 开始验证权限: " + method + " " + path);
            // 从请求头获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"未授权，请先登录\"}");
                return false;
            }

            String token = authHeader.substring(7);
            UserAccount user = tokenService.getUserByToken(token);
            
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"Token无效或已过期\"}");
                return false;
            }

            // 管理员角色（ADMIN）拥有所有权限
            Role role = user.getRole();
            if (role != null) {
                // 确保 role 对象已完全加载（在事务内访问属性）
                try {
                    String roleName = role.getName();
                    if ("ADMIN".equalsIgnoreCase(roleName)) {
                        return true;
                    }
                } catch (Exception e) {
                    // 如果访问 role 属性失败，说明懒加载有问题
                    // 这种情况下，拒绝访问
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"无法获取用户角色信息\"}");
                    return false;
                }
            }

            // 如果没有角色，拒绝访问
            if (role == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"用户未分配角色，无权限访问\"}");
                return false;
            }

            // 根据请求路径确定模块
            ModuleEntity module = findModuleByPath(path);
            if (module == null) {
                // 如果找不到对应的模块，拒绝访问（需要明确配置权限）
                System.out.println("[权限拦截] 未找到模块，拒绝访问: " + path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"未找到对应的模块配置，无权限访问\"}");
                return false;
            }

            System.out.println("[权限拦截] 找到模块: " + module.getZhName() + " (ID: " + module.getId() + ", Path: " + module.getPath() + ")");

            // 检查权限
            boolean hasPermission = checkPermission(role.getId(), module.getId(), method);
            if (!hasPermission) {
                System.out.println("[权限拦截] 权限检查失败，拒绝访问: " + method + " " + path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"无权限执行此操作\"}");
                return false;
            }

            System.out.println("[权限拦截] 权限检查通过，允许访问: " + method + " " + path);

            return true;
        } catch (Exception e) {
            // 捕获所有异常，避免500错误
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            String errorMsg = e.getMessage() != null ? e.getMessage() : "服务器内部错误";
            if (errorMsg.length() > 100) {
                errorMsg = errorMsg.substring(0, 100) + "...";
            }
            errorMsg = errorMsg.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
            response.getWriter().write("{\"message\":\"" + errorMsg + "\"}");
            return false;
        }
    }

    /**
     * 根据请求路径查找对应的模块
     */
    private ModuleEntity findModuleByPath(String path) {
        try {
            String originalPath = path;
            // 移除 /api 前缀
            if (path.startsWith("/api/")) {
                path = path.substring(5);
            } else if (path.startsWith("/api")) {
                path = path.substring(4);
            }

            // 移除查询参数
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }

            // 移除路径中的ID（如 /mall/ad-settings/123 -> /mall/ad-settings）
            String[] pathParts = path.split("/");
            if (pathParts.length > 2) {
                // 检查最后一段是否是UUID格式
                String lastPart = pathParts[pathParts.length - 1];
                if (lastPart.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}") ||
                    lastPart.matches("[0-9a-fA-F]{32}") ||
                    lastPart.matches("\\d+")) {
                    // 移除ID部分
                    path = String.join("/", java.util.Arrays.copyOf(pathParts, pathParts.length - 1));
                }
            }

            // 确保路径以 / 开头
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            // 特殊处理：注册相关接口也属于账号管理
            if (path.startsWith("/register")) {
                path = "/accounts";
            }

            System.out.println("[模块查找] 原始路径: " + originalPath + "，处理后路径: " + path);

            // 首先尝试精确匹配
            ModuleEntity module = moduleRepository.findByPath(path).orElse(null);
            
            // 如果精确匹配失败，尝试匹配父路径
            if (module == null && path.contains("/")) {
                String[] parts = path.split("/");
                // 从完整路径开始，逐步缩短路径进行匹配
                for (int i = parts.length; i > 1; i--) {
                    String testPath = String.join("/", java.util.Arrays.copyOf(parts, i));
                    if (!testPath.startsWith("/")) {
                        testPath = "/" + testPath;
                    }
                    System.out.println("[模块查找] 尝试匹配路径: " + testPath);
                    module = moduleRepository.findByPath(testPath).orElse(null);
                    if (module != null) {
                        System.out.println("[模块查找] 找到匹配模块: " + module.getZhName() + " (ID: " + module.getId() + ", Path: " + module.getPath() + ")");
                        break;
                    }
                }
            }

            if (module == null) {
                System.out.println("[模块查找] 未找到匹配的模块，路径: " + path);
            }

            return module;
        } catch (Exception e) {
            System.err.println("[模块查找] 查找模块时发生异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查用户是否有权限执行指定操作
     * @param roleId 角色ID
     * @param moduleId 模块ID
     * @param httpMethod HTTP方法（GET, POST, PUT, DELETE）
     * @return 是否有权限
     */
    private boolean checkPermission(UUID roleId, UUID moduleId, String httpMethod) {
        try {
            System.out.println("[权限检查] 开始检查权限 - 角色ID: " + roleId + ", 模块ID: " + moduleId + ", HTTP方法: " + httpMethod);
            
            // 直接查询该角色对该模块的权限（更高效）
            RoleModulePermission permission = permissionService.findByRoleAndModule(roleId, moduleId);
            
            if (permission == null) {
                System.out.println("[权限检查] 角色 " + roleId + " 对模块 " + moduleId + " 没有配置权限");
                return false; // 没有配置该模块的权限，拒绝访问
            }

            // 确保权限对象完全加载
            Boolean canRead = permission.getCanRead();
            Boolean canAdd = permission.getCanAdd();
            Boolean canUpdate = permission.getCanUpdate();
            
            System.out.println("[权限检查] 找到权限配置 - canRead=" + canRead + ", canAdd=" + canAdd + ", canUpdate=" + canUpdate);

            // 根据HTTP方法检查对应权限
            boolean hasPermission = false;
            switch (httpMethod.toUpperCase()) {
                case "GET":
                    hasPermission = Boolean.TRUE.equals(canRead);
                    System.out.println("[权限检查] GET请求，canRead=" + canRead + "，结果=" + hasPermission);
                    break;
                case "POST":
                    hasPermission = Boolean.TRUE.equals(canAdd);
                    System.out.println("[权限检查] POST请求，canAdd=" + canAdd + "，结果=" + hasPermission);
                    break;
                case "PUT":
                case "PATCH":
                    hasPermission = Boolean.TRUE.equals(canUpdate);
                    System.out.println("[权限检查] PUT/PATCH请求，canUpdate=" + canUpdate + "，结果=" + hasPermission);
                    break;
                case "DELETE":
                    hasPermission = Boolean.TRUE.equals(canUpdate); // 删除也视为更新操作
                    System.out.println("[权限检查] DELETE请求，canUpdate=" + canUpdate + "，结果=" + hasPermission);
                    break;
                default:
                    hasPermission = false;
                    System.out.println("[权限检查] 未知HTTP方法: " + httpMethod + "，拒绝访问");
            }
            
            return hasPermission;
        } catch (Exception e) {
            // 如果检查权限时出现异常，拒绝访问
            System.err.println("[权限检查] 检查权限时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

