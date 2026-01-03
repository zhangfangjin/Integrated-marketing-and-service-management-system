package org.example.rootmanage.permission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PermissionUpdateRequest {
    @NotNull
    private UUID roleId;
    @NotNull
    private List<PermissionItem> permissions;
}

