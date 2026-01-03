package org.example.rootmanage.permission;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.module.ModuleEntity;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.module.dto.ModuleTreeNode;
import org.example.rootmanage.permission.dto.ModulePermissionTreeNode;
import org.example.rootmanage.permission.dto.PermissionItem;
import org.example.rootmanage.permission.dto.PermissionUpdateRequest;
import org.example.rootmanage.role.Role;
import org.example.rootmanage.role.RoleRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleModulePermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;

    @Transactional(readOnly = true)
    public List<RoleModulePermission> findByRole(UUID roleId) {
        List<RoleModulePermission> permissions = permissionRepository.findByRoleId(roleId);
        // 确保所有懒加载的module对象都被初始化
        for (RoleModulePermission p : permissions) {
            if (p != null && p.getModule() != null) {
                Hibernate.initialize(p.getModule());
            }
        }
        return permissions;
    }

    @Transactional(readOnly = true)
    public RoleModulePermission findByRoleAndModule(UUID roleId, UUID moduleId) {
        return permissionRepository.findByRoleIdAndModuleId(roleId, moduleId).orElse(null);
    }

    @Transactional
    public void savePermissions(PermissionUpdateRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));

        // 简单做法：清除后重建
        permissionRepository.deleteByRoleId(role.getId());

        for (PermissionItem item : request.getPermissions()) {
            ModuleEntity module = moduleRepository.findById(item.getModuleId())
                    .orElseThrow(() -> new IllegalArgumentException("模块不存在: " + item.getModuleId()));
            RoleModulePermission perm = new RoleModulePermission();
            perm.setRole(role);
            perm.setModule(module);
            perm.setCanRead(item.isCanRead());
            perm.setCanAdd(item.isCanAdd());
            perm.setCanUpdate(item.isCanUpdate());
            perm.setCanSee(item.isCanSee());
            permissionRepository.save(perm);
        }
    }

    /**
     * 获取带权限状态的模块树（用于权限配置界面）
     */
    public List<ModulePermissionTreeNode> getModuleTreeWithPermissions(UUID roleId) {
        List<ModuleEntity> modules = moduleRepository.findAll();
        modules.sort(Comparator.comparing(ModuleEntity::getOrderNo));

        // 获取该角色已配置的权限
        List<RoleModulePermission> permissions = permissionRepository.findByRoleId(roleId);
        Map<UUID, RoleModulePermission> permMap = permissions.stream()
                .collect(Collectors.toMap(p -> p.getModule().getId(), p -> p));

        // 构建节点映射
        Map<UUID, ModulePermissionTreeNode> nodeMap = new HashMap<>();
        for (ModuleEntity module : modules) {
            ModulePermissionTreeNode node = toPermissionNode(module, permMap.get(module.getId()));
            nodeMap.put(module.getId(), node);
        }

        // 构建树结构
        List<ModulePermissionTreeNode> roots = new ArrayList<>();
        for (ModuleEntity module : modules) {
            ModulePermissionTreeNode node = nodeMap.get(module.getId());
            if (module.getParent() == null) {
                roots.add(node);
            } else {
                ModulePermissionTreeNode parent = nodeMap.get(module.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(node);
                    parent.getChildren().sort(Comparator.comparing(ModulePermissionTreeNode::getOrderNo));
                }
            }
        }

        return roots;
    }

    private ModulePermissionTreeNode toPermissionNode(ModuleEntity module, RoleModulePermission permission) {
        ModulePermissionTreeNode node = new ModulePermissionTreeNode();
        node.setId(module.getId());
        node.setZhName(module.getZhName());
        node.setEnName(module.getEnName());
        node.setLevel(module.getLevel());
        node.setOrderNo(module.getOrderNo());
        node.setPath(module.getPath());
        node.setIcon(module.getIcon());
        node.setGroupCode(module.getGroupCode());
        node.setPermissionKey(module.getPermissionKey());
        node.setParentNode(Boolean.TRUE.equals(module.getParentNode()));
        node.setExpanded(Boolean.TRUE.equals(module.getExpanded()));
        node.setVisible(Boolean.TRUE.equals(module.getVisible()));

        if (permission != null) {
            node.setSelected(true);
            node.setCanRead(Boolean.TRUE.equals(permission.getCanRead()));
            node.setCanAdd(Boolean.TRUE.equals(permission.getCanAdd()));
            node.setCanUpdate(Boolean.TRUE.equals(permission.getCanUpdate()));
            node.setCanSee(Boolean.TRUE.equals(permission.getCanSee()));
        } else {
            node.setSelected(false);
            node.setCanRead(false);
            node.setCanAdd(false);
            node.setCanUpdate(false);
            node.setCanSee(false);
        }

        return node;
    }

    /**
     * 获取角色有权限访问的模块树（只返回 canSee=true 的模块，用于登录后显示菜单）
     */
    @Transactional(readOnly = true)
    public List<ModuleTreeNode> getAccessibleModuleTree(UUID roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }

        // 获取角色信息
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }

        // 管理员角色（ADMIN）默认拥有所有模块的访问权限
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role.getName());
        
        // 获取所有模块
        List<ModuleEntity> allModules = moduleRepository.findAll();
        allModules.sort(Comparator.comparing(ModuleEntity::getOrderNo));

        Set<UUID> accessibleModuleIds;
        if (isAdmin) {
            // 管理员：返回所有模块
            accessibleModuleIds = allModules.stream()
                    .map(ModuleEntity::getId)
                    .collect(Collectors.toSet());
        } else {
            // 其他角色：根据配置的权限
            List<RoleModulePermission> permissions = permissionRepository.findByRoleId(roleId);
            
            // 只保留 canSee=true 的权限
            accessibleModuleIds = permissions.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getCanSee()))
                    .map(p -> {
                        Hibernate.initialize(p.getModule());
                        return p.getModule().getId();
                    })
                    .collect(Collectors.toSet());

            if (accessibleModuleIds.isEmpty()) {
                return new ArrayList<>();
            }
        }

        // 过滤出有权限的模块，并包含所有父模块（即使父模块没有权限，只要有子模块有权限也要显示）
        Map<UUID, ModuleEntity> accessibleModules = new HashMap<>();
        for (ModuleEntity module : allModules) {
            if (accessibleModuleIds.contains(module.getId())) {
                accessibleModules.put(module.getId(), module);
                // 添加所有父模块
                ModuleEntity parent = module.getParent();
                while (parent != null && !accessibleModules.containsKey(parent.getId())) {
                    Hibernate.initialize(parent);
                    accessibleModules.put(parent.getId(), parent);
                    parent = parent.getParent();
                }
            }
        }

        // 构建节点映射
        Map<UUID, ModuleTreeNode> nodeMap = new HashMap<>();
        for (ModuleEntity module : accessibleModules.values()) {
            ModuleTreeNode node = toModuleTreeNode(module);
            nodeMap.put(module.getId(), node);
        }

        // 构建树结构
        List<ModuleTreeNode> roots = new ArrayList<>();
        for (ModuleEntity module : accessibleModules.values()) {
            ModuleTreeNode node = nodeMap.get(module.getId());
            if (module.getParent() == null) {
                roots.add(node);
            } else {
                ModuleTreeNode parent = nodeMap.get(module.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(node);
                    parent.getChildren().sort(Comparator.comparing(ModuleTreeNode::getOrderNo));
                }
            }
        }

        roots.sort(Comparator.comparing(ModuleTreeNode::getOrderNo));
        return roots;
    }

    private ModuleTreeNode toModuleTreeNode(ModuleEntity m) {
        ModuleTreeNode node = new ModuleTreeNode();
        node.setId(m.getId());
        node.setZhName(m.getZhName());
        node.setEnName(m.getEnName());
        node.setLevel(m.getLevel());
        node.setOrderNo(m.getOrderNo());
        node.setPath(m.getPath());
        node.setIcon(m.getIcon());
        node.setGroupCode(m.getGroupCode());
        node.setPermissionKey(m.getPermissionKey());
        node.setParentNode(Boolean.TRUE.equals(m.getParentNode()));
        node.setExpanded(Boolean.TRUE.equals(m.getExpanded()));
        node.setVisible(Boolean.TRUE.equals(m.getVisible()));
        return node;
    }
}

