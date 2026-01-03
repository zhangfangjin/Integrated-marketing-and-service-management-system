package org.example.rootmanage.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RegistrationReviewRequest {
    @NotNull
    private UUID roleId;
    private String remark;
}

