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
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售报价管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_quotation")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesQuotation extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String quotationNo; // 报价单号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // 客户

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id")
    private UserAccount salesperson; // 销售负责人

    @Column(nullable = false)
    private LocalDate quotationDate; // 报价日期

    private LocalDate validUntil; // 有效期至

    @Column(nullable = false)
    private BigDecimal totalAmount; // 总金额

    private String projectName; // 项目名称

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OptionItem status; // 状态（待审核、已审核、已接受、已拒绝等）

    private String quotationContent; // 报价内容（JSON格式存储明细）

    private String terms; // 条款说明

    private String remark; // 备注
}

