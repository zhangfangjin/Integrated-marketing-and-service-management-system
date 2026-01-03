package org.example.rootmanage.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank
    private String name;
    private String description;
}

