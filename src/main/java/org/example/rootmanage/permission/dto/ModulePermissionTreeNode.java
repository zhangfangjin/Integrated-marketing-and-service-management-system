package org.example.rootmanage.permission.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 带权限状态的模块树节点，用于权限配置界面
 */
@Data
public class ModulePermissionTreeNode {
    private UUID id;
    private String zhName;
    private String enName;
    private Integer level;
    private Integer orderNo;
    private String path;
    private String icon;
    private String groupCode;
    private String permissionKey;
    private Boolean parentNode;
    private Boolean expanded;
    private Boolean visible;
    
    // 权限配置相关
    private boolean selected;  // 是否已为该角色配置
    private boolean canRead;
    private boolean canAdd;
    private boolean canUpdate;
    private boolean canSee;
    
    private List<ModulePermissionTreeNode> children = new ArrayList<>();
}

