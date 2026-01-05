package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.contract.entity.Contract;

import java.sql.Date;

/**
 * 返厂泵实体类
 * 用于存储返厂泵信息（从重泵公司自制收发货平台查询）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "returned_pump")
public class ReturnedPump extends BaseEntity {

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
     * 返厂泵名称
     */
    @Column(nullable = false)
    private String pumpName;

    /**
     * 返厂时间
     */
    @Column(nullable = true)
    private Date returnDate;

    /**
     * 泵型号
     */
    @Column(nullable = true)
    private String pumpModel;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

