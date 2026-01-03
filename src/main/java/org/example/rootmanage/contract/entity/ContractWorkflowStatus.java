package org.example.rootmanage.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.rootmanage.common.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 合同流程审批状态实体类
 * 用于存储合同的审批流程状态信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_workflow_status")
public class ContractWorkflowStatus extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 节点名称
     */
    @Column(nullable = false)
    private String nodeName;

    /**
     * 操作者ID（关联用户表）
     */
    @Column(nullable = true)
    private UUID operatorId;

    /**
     * 操作者姓名
     */
    @Column(nullable = true)
    private String operatorName;

    /**
     * 状态（已提交、已通过、已拒绝、未查看等）
     */
    @Column(nullable = false)
    private String status;

    /**
     * 操作时间
     */
    @Column(nullable = true)
    private LocalDateTime operationTime;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

