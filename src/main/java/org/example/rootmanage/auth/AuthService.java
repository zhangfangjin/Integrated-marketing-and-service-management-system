package org.example.rootmanage.auth;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.AccountStatus;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.auth.dto.LoginRequest;
import org.example.rootmanage.auth.dto.LoginResponse;
import org.example.rootmanage.module.dto.ModuleTreeNode;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.role.Role;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            UserAccount user = userAccountRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

            if (user.getStatus() != AccountStatus.APPROVED) {
                throw new IllegalStateException("账号未审核通过或已被禁用");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("用户名或密码错误");
            }

            // 初始化懒加载的 role 对象，确保在 Session 关闭前加载
            Role role = null;
            if (user.getRole() != null) {
                Hibernate.initialize(user.getRole());
                role = user.getRole();
                // 访问属性确保完全加载
                if (role != null) {
                    role.getName();
                    role.getDescription();
                }
            }

            // 简单实现：生成一个简单的token（实际应使用JWT）
            String token = UUID.randomUUID().toString().replace("-", "");

            // 存储token和用户ID的映射
            tokenService.storeToken(token, user.getId());

            // 获取用户角色有权限访问的模块树
            List<ModuleTreeNode> modules = role != null 
                    ? permissionService.getAccessibleModuleTree(role.getId())
                    : new ArrayList<>();

            return new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    token,
                    role,
                    modules
            );
        } catch (Exception e) {
            System.err.println("[登录服务] 登录失败: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常，让异常处理器处理
        }
    }
}

