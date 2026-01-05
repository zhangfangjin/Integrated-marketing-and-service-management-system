package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.contract.entity.Contract;

import java.sql.Date;
import java.util.UUID;

/**
 * 售后服务单实体类
 * 用于存储售后服务请求信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "after_sales_order")
public class AfterSalesOrder extends BaseEntity {

    /**
     * 服务单号
     */
    @Column(nullable = false, unique = true)
    private String serviceOrderNumber;

    /**
     * 售后类型（故障处理、安装调试、有偿售后、无偿三包）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AfterSalesType serviceType;

    /**
     * 关联的合同
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = true)
    private Contract contract;

    /**
     * 合同编号（冗余字段，方便查询）
     */
    @Column(nullable = true)
    private String contractNumber;

    /**
     * 合同名称（冗余字段，方便查询）
     */
    @Column(nullable = true)
    private String contractName;

    /**
     * 业主单位（客户单位）
     */
    @Column(nullable = true)
    private String customerUnit;

    /**
     * 付款计划
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String paymentPlan;

    /**
     * 售后时间
     */
    @Column(nullable = true)
    private Date serviceDate;

    /**
     * 地区
     */
    @Column(nullable = true)
    private String region;

    /**
     * 维修方案附件路径
     */
    @Column(nullable = true)
    private String maintenancePlanAttachment;

    /**
     * 换件清单附件路径
     */
    @Column(nullable = true)
    private String replacementPartsListAttachment;

    /**
     * 客户请求附件路径
     */
    @Column(nullable = true)
    private String customerRequestAttachment;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;

    /**
     * 受理人员ID（指派处理人员）
     */
    @Column(nullable = true)
    private UUID handlerId;

    /**
     * 受理人员姓名（冗余字段）
     */
    @Column(nullable = true)
    private String handlerName;

    /**
     * 服务状态（待分配、处理中、已完成、已取消等）
     */
    @Column(nullable = false)
    private String serviceStatus = "待分配";

    /**
     * 是否需要审批（无偿三包类型需要审批流程）
     */
    @Column(nullable = false)
    private Boolean needsApproval = false;

    /**
     * 审批状态（待审批、审批中、已通过、已拒绝）
     */
    @Column(nullable = true)
    private String approvalStatus;

    /**
     * 完成日期
     */
    @Column(nullable = true)
    private Date completionDate;

    /**
     * 设备编号
     */
    @Column(nullable = true)
    private String deviceNumber;

    /**
     * 设备名称
     */
    @Column(nullable = true)
    private String deviceName;

    /**
     * 售后总结
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String serviceSummary;

    /**
     * 售后评价（优、良、中、差等）
     */
    @Column(nullable = true)
    private String evaluation;

    /**
     * 评价人ID
     */
    @Column(nullable = true)
    private UUID evaluatorId;

    /**
     * 评价人姓名
     */
    @Column(nullable = true)
    private String evaluatorName;

    /**
     * 评价时间
     */
    @Column(nullable = true)
    private java.time.LocalDateTime evaluationTime;

    /**
     * 是否已关闭
     */
    @Column(nullable = false)
    private Boolean isClosed = false;
}

