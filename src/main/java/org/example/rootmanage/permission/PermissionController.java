package org.example.rootmanage.permission;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.permission.dto.ModulePermissionTreeNode;
import org.example.rootmanage.permission.dto.PermissionUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/role/{roleId}")
    public List<RoleModulePermission> listByRole(@PathVariable UUID roleId) {
        return permissionService.findByRole(roleId);
    }

    @PostMapping
    public void save(@RequestBody PermissionUpdateRequest request) {
        permissionService.savePermissions(request);
    }

    /**
     * 获取带权限状态的模块树（用于权限配置界面）
     */
    @GetMapping("/role/{roleId}/tree")
    public List<ModulePermissionTreeNode> getModuleTreeWithPermissions(@PathVariable UUID roleId) {
        return permissionService.getModuleTreeWithPermissions(roleId);
    }
}

