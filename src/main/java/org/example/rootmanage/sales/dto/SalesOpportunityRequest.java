package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SalesOpportunityRequest {

    @NotNull(message = "客户ID不能为空")
    private UUID customerId;

    private UUID productId;

    @NotNull(message = "机会名称不能为空")
    private String opportunityName;

    private String opportunitySubject; // 销售机会主题

    private LocalDate opportunityDate; // 销售机会日期

    private UUID opportunityStageId;

    private UUID salespersonId;

    private BigDecimal estimatedAmount;

    private BigDecimal budget; // 预算

    private LocalDate expectedCloseDate;

    private BigDecimal successProbability;

    private String inventory; // 存货

    private UUID leadSourceId; // 线索来源

    private Boolean received = false; // 接收状态

    private Boolean submitted = false; // 提交状态

    private String description;

    private String remark;

    private UUID statusId;
}

