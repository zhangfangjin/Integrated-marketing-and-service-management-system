package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SalesInventoryRequest {

    @NotNull(message = "产品ID不能为空")
    private UUID productId;

    private UUID warehouseId;

    @NotNull(message = "库存数量不能为空")
    private Integer quantity;

    @NotNull(message = "可用数量不能为空")
    private Integer availableQuantity;

    private Integer reservedQuantity;

    private BigDecimal unitPrice;

    private BigDecimal totalValue;

    private String location;

    private String drawingNo; // 图号

    private String material; // 材料

    private Boolean isStagnant = false; // 是否呆滞

    private String remark;
}

