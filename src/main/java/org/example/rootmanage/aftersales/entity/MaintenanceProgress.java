package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.contract.entity.Contract;

import java.sql.Date;

/**
 * 维修进度实体类
 * 用于存储三包及有偿维修合同的维修进度信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "maintenance_progress")
public class MaintenanceProgress extends BaseEntity {

    /**
     * 关联的合同
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 合同编号（冗余字段）
     */
    @Column(nullable = true)
    private String contractNumber;

    /**
     * 合同名称（冗余字段）
     */
    @Column(nullable = true)
    private String contractName;

    /**
     * 合同类型
     */
    @Column(nullable = true)
    private String contractType;

    /**
     * 换件清单附件路径
     */
    @Column(nullable = true)
    private String replacementPartsListAttachment;

    /**
     * 拆解报告附件路径
     */
    @Column(nullable = true)
    private String disassemblyReportAttachment;

    /**
     * 计划附件路径（运行部下的计划）
     */
    @Column(nullable = true)
    private String planAttachment;

    /**
     * 扣套时间（装配车间）
     */
    @Column(nullable = true)
    private Date deductionTime;

    /**
     * 预计发货时间
     */
    @Column(nullable = true)
    private Date estimatedDeliveryTime;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

