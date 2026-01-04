package org.example.rootmanage.pricebook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.basicinfo.entity.Product;

import java.math.BigDecimal;

/**
 * 价格本实体类
 * 用于存储产品的价格信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "price_book")
public class PriceBook extends BaseEntity {

    /**
     * 关联的产品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 价格版本号
     */
    @Column(nullable = false)
    private String versionNumber;

    /**
     * 价格类型（标准价格、成本价格、销售价格等）
     */
    @Column(nullable = false)
    private String priceType;

    /**
     * 单价
     */
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 货币单位
     */
    @Column(nullable = false)
    private String currency = "CNY";

    /**
     * 生效日期
     */
    @Column(nullable = true)
    private java.sql.Date effectiveDate;

    /**
     * 失效日期
     */
    @Column(nullable = true)
    private java.sql.Date expiryDate;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

