package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.sql.Date;
import java.util.UUID;

/**
 * 售后服务活动实体类
 * 用于记录售后服务过程中的活动信息（跟踪记录）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "after_sales_activity")
public class AfterSalesActivity extends BaseEntity {

    /**
     * 关联的售后服务单
     */
    @ManyToOne
    @JoinColumn(name = "after_sales_order_id", nullable = false)
    private AfterSalesOrder afterSalesOrder;

    /**
     * 活动类型（跟踪、完成、评价等）
     */
    @Column(nullable = false)
    private String activityType;

    /**
     * 活动时间
     */
    @Column(nullable = true)
    private Date activityDate;

    /**
     * 说明（情况说明）
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    /**
     * 操作人ID
     */
    @Column(nullable = true)
    private UUID operatorId;

    /**
     * 操作人姓名
     */
    @Column(nullable = true)
    private String operatorName;
}

















