package org.example.rootmanage.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.rootmanage.module.dto.ModuleTreeNode;
import org.example.rootmanage.role.Role;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UUID userId;
    private String username;
    private String name;
    private String token;  // 简单实现，实际应使用JWT
    private Role role;
    private List<ModuleTreeNode> modules;  // 用户有权限访问的模块树
}

