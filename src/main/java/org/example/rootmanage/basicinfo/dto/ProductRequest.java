package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "产品编码不能为空")
    private String productCode;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    private UUID productTypeId;

    private UUID productCategoryId;

    private String specification;

    private String unit;

    private BigDecimal standardPrice;

    private BigDecimal costPrice;

    private String description;

    // 扩展字段（用于单体设备、备品备件、设备成套）
    private String size; // 尺寸

    private String weight; // 重量

    private String deliveryCycle; // 交货周期

    private String attachments; // 附件

    private String remark;

    private Boolean active = true;
}

