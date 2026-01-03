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
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.math.BigDecimal;

/**
 * 销售库存查询实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_inventory")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesInventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 产品

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private OptionItem warehouse; // 仓库

    @Column(nullable = false)
    private Integer quantity; // 库存数量

    @Column(nullable = false)
    private Integer availableQuantity; // 可用数量

    private Integer reservedQuantity; // 预留数量

    private BigDecimal unitPrice; // 单价

    private BigDecimal totalValue; // 总价值

    private String location; // 存放位置

    private String drawingNo; // 图号

    private String material; // 材料

    @Column(nullable = false)
    private Boolean isStagnant = false; // 是否呆滞

    private String remark; // 备注
}

