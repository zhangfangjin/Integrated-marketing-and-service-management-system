package org.example.rootmanage.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.example.rootmanage.common.BaseEntity;

/**
 * 合同执行进度实体类
 * 用于存储合同的执行进度信息（设计、生产、采购、制造、装配）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_execution_progress")
public class ContractExecutionProgress extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 设计进度（已完成、未完成、进行中等）
     */
    @Column(nullable = true)
    private String designProgress;

    /**
     * 生产进度（已完成、未完成、进行中等）
     */
    @Column(nullable = true)
    private String productionProgress;

    /**
     * 采购进度（已完成、未完成、进行中等）
     */
    @Column(nullable = true)
    private String procurementProgress;

    /**
     * 制造进度（已完成、未完成、进行中等）
     */
    @Column(nullable = true)
    private String manufacturingProgress;

    /**
     * 装配进度（已完成、未完成、进行中等）
     */
    @Column(nullable = true)
    private String assemblyProgress;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

