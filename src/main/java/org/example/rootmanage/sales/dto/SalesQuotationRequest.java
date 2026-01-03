package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SalesQuotationRequest {

    @NotNull(message = "报价单号不能为空")
    private String quotationNo;

    @NotNull(message = "客户ID不能为空")
    private UUID customerId;

    private UUID salespersonId;

    @NotNull(message = "报价日期不能为空")
    private LocalDate quotationDate;

    private LocalDate validUntil;

    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    private String projectName; // 项目名称

    private UUID statusId;

    private String quotationContent;

    private String terms;

    private String remark;
}

