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
 * 合同细目实体类
 * 用于存储合同的产品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "contract_detail")
public class ContractDetail extends BaseEntity {

    /**
     * 关联的合同ID
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * 类型
     */
    @Column(nullable = true)
    private String type;

    /**
     * 产品型号
     */
    @Column(nullable = true)
    private String productModel;

    /**
     * 产品类型
     */
    @Column(nullable = true)
    private String productType;

    /**
     * 细分类型
     */
    @Column(nullable = true)
    private String subType;

    /**
     * 产品名称
     */
    @Column(nullable = true)
    private String productName;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

