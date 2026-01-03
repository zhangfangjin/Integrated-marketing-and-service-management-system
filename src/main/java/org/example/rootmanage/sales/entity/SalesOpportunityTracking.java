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
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

import java.time.LocalDateTime;

/**
 * 销售机会跟踪实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_opportunity_tracking")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesOpportunityTracking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private SalesOpportunity opportunity; // 销售机会

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OptionItem status; // 跟踪状态（跟踪、报价等）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracker_id")
    private UserAccount tracker; // 跟踪人（拜访人）

    @Column(nullable = false)
    private LocalDateTime trackingTime; // 拜访时间

    private String matter; // 事宜

    private String description; // 情况说明

    private String attachments; // 附件（存储附件路径，多个用逗号分隔）

    private String remark; // 备注
}

