package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SalesInventoryReductionRequest {

    @NotNull(message = "降库单号不能为空")
    private String reductionNo;

    @NotNull(message = "产品ID不能为空")
    private UUID productId;

    @NotNull(message = "降库数量不能为空")
    private Integer quantity;

    private UUID warehouseId;

    @NotNull(message = "降库日期不能为空")
    private LocalDate reductionDate;

    private UUID reductionTypeId;

    private UUID operatorId;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private String reason;

    private String remark;
}

