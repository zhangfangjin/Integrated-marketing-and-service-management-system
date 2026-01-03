package org.example.rootmanage.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.util.UUID;

import org.example.rootmanage.common.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 合同审批节点实体类
 * 用于存储合同的审批节点配置信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_approval_node")
public class ContractApprovalNode extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 审批角色类型（片区负责人、部门负责人、公司领导、财务总监）
     */
    @Column(nullable = false)
    private String roleType;

    /**
     * 审核人ID（关联用户表）
     */
    @Column(nullable = true)
    private UUID approverId;

    /**
     * 审核人姓名
     */
    @Column(nullable = true)
    private String approverName;

    /**
     * 审批顺序（用于确定审批流程的顺序）
     */
    @Column(nullable = false)
    private Integer approvalOrder;
}

