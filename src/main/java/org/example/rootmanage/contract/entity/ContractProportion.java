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
 * 合同占比划分实体类
 * 用于存储合同的占比划分信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_proportion")
public class ContractProportion extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 负责人ID（关联用户表）
     */
    @Column(nullable = true)
    private UUID personInChargeId;

    /**
     * 负责人姓名
     */
    @Column(nullable = true)
    private String personInChargeName;

    /**
     * 所属片区
     */
    @Column(nullable = true)
    private String affiliatedArea;

    /**
     * 占比划分（百分比）
     */
    @Column(nullable = true)
    private Double proportion;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

