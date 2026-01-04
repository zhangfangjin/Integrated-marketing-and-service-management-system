package org.example.rootmanage.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.sql.Date;

import org.example.rootmanage.common.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 合同付款阶段实体类
 * 用于存储合同的付款阶段信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_payment_stage")
public class ContractPaymentStage extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 付款阶段
     */
    @Column(nullable = true)
    private String paymentStage;

    /**
     * 应付金额
     */
    @Column(nullable = true)
    private Double amountPayable;

    /**
     * 已付金额
     */
    @Column(nullable = true)
    private Double amountPaid;

    /**
     * 付款阶段名称
     */
    @Column(nullable = true)
    private String paymentStageName;

    /**
     * 应付时间
     */
    @Column(nullable = true)
    private Date dueDate;

    /**
     * 付款日期
     */
    @Column(nullable = true)
    private Date paymentDate;

    /**
     * 责任人
     */
    @Column(nullable = true)
    private String responsiblePerson;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

