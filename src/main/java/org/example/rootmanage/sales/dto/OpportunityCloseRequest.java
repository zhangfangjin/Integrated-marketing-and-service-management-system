package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OpportunityCloseRequest {

    @NotNull(message = "销售机会ID不能为空")
    private UUID opportunityId;

    @NotBlank(message = "关闭原因不能为空")
    private String closeReason;
}

