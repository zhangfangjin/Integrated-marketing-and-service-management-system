package org.example.rootmanage.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ModuleRequest {
    @NotBlank
    private String zhName;
    @NotBlank
    private String enName;
    @NotNull
    private Integer level;
    @NotNull
    private Integer orderNo;
    private String path;
    private String icon;
    private String groupCode;
    private String permissionKey;
    private UUID parentId;
    private Boolean parentNode = false;
    private Boolean expanded = false;
    private Boolean visible = true;
}

