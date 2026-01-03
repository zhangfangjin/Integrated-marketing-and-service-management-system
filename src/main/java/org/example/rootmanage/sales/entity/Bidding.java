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
 * 投标管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "bidding")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bidding extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String biddingNo; // 投标编号

    @Column(nullable = false)
    private String biddingName; // 投标名称

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // 投标客户

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private org.example.rootmanage.sales.entity.SalesOpportunity opportunity; // 关联的销售机会

    private String opportunityName; // 机会名称（冗余字段，便于查询）

    private String opportunityNo; // 机会编号

    @Column(nullable = false)
    private String projectName; // 项目名称

    @Column(nullable = false)
    private LocalDate biddingDate; // 投标时间

    private LocalDate deadline; // 截止日期

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidding_type_id")
    private OptionItem biddingType; // 投标类型（机会投标等）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_person_id")
    private UserAccount responsiblePerson; // 负责人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidding_status_id")
    private OptionItem biddingStatus; // 投标状态（准备中、已提交、已中标、未中标、流标、废标等）

    private BigDecimal biddingAmount; // 投标金额（价格）

    private BigDecimal estimatedProfit; // 预计利润

    private String technicalSolution; // 技术方案

    private String quotationSheet; // 报价单

    private String biddingSummary; // 投标总结内容

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_status_id")
    private OptionItem summaryStatus; // 投标总结状态（已中、未中、流标、废标）

    private String attachments; // 附件（存储附件路径，多个用逗号分隔）

    private String projectDescription; // 项目描述

    private String biddingContent; // 投标内容

    private String competitorInfo; // 竞争对手信息

    private String remark; // 备注
}

