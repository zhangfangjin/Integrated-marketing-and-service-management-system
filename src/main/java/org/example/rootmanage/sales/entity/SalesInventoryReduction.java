package org.example.rootmanage.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售降库管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_inventory_reduction")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesInventoryReduction extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String reductionNo; // 降库单号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 产品

    @Column(nullable = false)
    private Integer quantity; // 降库数量

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private OptionItem warehouse; // 仓库

    @Column(nullable = false)
    private LocalDate reductionDate; // 降库日期

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reduction_type_id")
    private OptionItem reductionType; // 降库类型（销售出库、损耗、调拨等）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private UserAccount operator; // 操作人

    private BigDecimal unitPrice; // 单价

    private BigDecimal totalAmount; // 总金额

    private String reason; // 降库原因

    private String remark; // 备注
}

