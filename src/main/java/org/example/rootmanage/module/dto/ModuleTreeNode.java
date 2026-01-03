package org.example.rootmanage.module.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ModuleTreeNode {
    private UUID id;
    private String zhName;
    private String enName;
    private Integer level;
    private Integer orderNo;
    private String path;
    private String icon;
    private String groupCode;
    private String permissionKey;
    private boolean parentNode;
    private boolean expanded;
    private boolean visible;
    private List<ModuleTreeNode> children = new ArrayList<>();
}

