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
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售机会管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_opportunity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesOpportunity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // 客户

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 产品

    @Column(nullable = false)
    private String opportunityName; // 机会名称

    private String opportunitySubject; // 销售机会主题

    @Column(nullable = false)
    private LocalDate opportunityDate; // 销售机会日期

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_stage_id")
    private OptionItem opportunityStage; // 机会阶段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id")
    private UserAccount salesperson; // 销售负责人

    private BigDecimal estimatedAmount; // 预计金额

    private BigDecimal budget; // 预算

    private LocalDate expectedCloseDate; // 预计成交日期

    private BigDecimal successProbability; // 成功概率（0-100）

    private String inventory; // 存货

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_source_id")
    private OptionItem leadSource; // 线索来源

    @Column(nullable = false)
    private Boolean received = false; // 接收状态（已接收/未接收）

    @Column(nullable = false)
    private Boolean submitted = false; // 提交状态（是否已提交）

    private String description; // 机会描述

    private String remark; // 备注

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OptionItem status; // 状态（进行中、已成交、已流失、待分配、已关闭等）

    private String closeReason; // 关闭原因
}

