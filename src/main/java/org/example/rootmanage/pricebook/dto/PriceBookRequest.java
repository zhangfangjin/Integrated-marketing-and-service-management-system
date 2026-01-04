package org.example.rootmanage.pricebook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 价格本请求DTO
 */
@Data
public class PriceBookRequest {

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private UUID productId;

    /**
     * 价格版本号
     */
    @NotBlank(message = "价格版本号不能为空")
    private String versionNumber;

    /**
     * 价格类型（标准价格、成本价格、销售价格等）
     */
    @NotBlank(message = "价格类型不能为空")
    private String priceType;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    /**
     * 货币单位
     */
    private String currency = "CNY";

    /**
     * 生效日期
     */
    private java.sql.Date effectiveDate;

    /**
     * 失效日期
     */
    private java.sql.Date expiryDate;

    /**
     * 是否启用
     */
    private Boolean active = true;

    /**
     * 备注
     */
    private String remark;
}

