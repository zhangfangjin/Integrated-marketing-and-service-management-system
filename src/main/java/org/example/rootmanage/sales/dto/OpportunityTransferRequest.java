package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OpportunityTransferRequest {

    @NotNull(message = "销售机会ID不能为空")
    private UUID opportunityId;

    @NotEmpty(message = "目标片区ID列表不能为空")
    private List<UUID> targetAreaIds;
}

