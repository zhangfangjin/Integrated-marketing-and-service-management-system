package org.example.rootmanage.basicinfo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.math.BigDecimal;

/**
 * 产品管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "product")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String productCode; // 产品编码

    @Column(nullable = false)
    private String productName; // 产品名称

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    private OptionItem productType; // 产品类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private OptionItem productCategory; // 产品分类

    private String specification; // 规格型号

    private String unit; // 单位

    private BigDecimal standardPrice; // 标准价格

    private BigDecimal costPrice; // 成本价格

    private String description; // 产品描述

    // 扩展字段（用于单体设备、备品备件、设备成套）
    private String size; // 尺寸

    private String weight; // 重量

    private String deliveryCycle; // 交货周期

    private String attachments; // 附件（存储附件路径，多个用逗号分隔）

    private String remark; // 备注

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

