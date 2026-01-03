package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SalesOpportunityTrackingRequest {

    @NotNull(message = "销售机会ID不能为空")
    private UUID opportunityId;

    private UUID statusId; // 跟踪状态

    private UUID trackerId; // 跟踪人ID

    @NotNull(message = "跟踪时间不能为空")
    private LocalDateTime trackingTime; // 拜访时间

    private String matter; // 事宜

    private String description; // 情况说明

    private String attachments; // 附件

    private String remark; // 备注
}

