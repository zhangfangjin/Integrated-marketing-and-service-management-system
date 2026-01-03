package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class BiddingRequest {

    @NotNull(message = "投标编号不能为空")
    private String biddingNo;

    @NotNull(message = "投标名称不能为空")
    private String biddingName;

    @NotNull(message = "客户ID不能为空")
    private UUID customerId;

    private UUID opportunityId; // 关联的销售机会ID

    private String opportunityName; // 机会名称

    private String opportunityNo; // 机会编号

    @NotNull(message = "项目名称不能为空")
    private String projectName;

    @NotNull(message = "投标日期不能为空")
    private LocalDate biddingDate;

    private LocalDate deadline;

    private UUID biddingTypeId; // 投标类型

    private UUID responsiblePersonId;

    private UUID biddingStatusId;

    private BigDecimal biddingAmount; // 价格

    private BigDecimal estimatedProfit;

    private String technicalSolution; // 技术方案

    private String quotationSheet; // 报价单

    private String biddingSummary; // 投标总结内容

    private UUID summaryStatusId; // 投标总结状态（已中、未中、流标、废标）

    private String attachments; // 附件

    private String projectDescription;

    private String biddingContent;

    private String competitorInfo;

    private String remark;
}

