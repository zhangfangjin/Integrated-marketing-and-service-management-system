package org.example.rootmanage.permission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PermissionItem {
    @NotNull
    private UUID moduleId;
    private boolean canRead = true;
    private boolean canAdd = false;
    private boolean canUpdate = false;
    private boolean canSee = true;
}

