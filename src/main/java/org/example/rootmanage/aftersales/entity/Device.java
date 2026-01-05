package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.contract.entity.Contract;

/**
 * 设备实体类
 * 用于存储合同关联的设备信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "device")
public class Device extends BaseEntity {

    /**
     * 设备编号
     */
    @Column(nullable = false, unique = true)
    private String deviceNumber;

    /**
     * 设备名称
     */
    @Column(nullable = false)
    private String deviceName;

    /**
     * 关联的合同
     */
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = true)
    private Contract contract;

    /**
     * 合同编号（冗余字段）
     */
    @Column(nullable = true)
    private String contractNumber;

    /**
     * 设备型号
     */
    @Column(nullable = true)
    private String deviceModel;

    /**
     * 出厂时间（生产时间）
     */
    @Column(nullable = true)
    private java.sql.Date productionDate;

    /**
     * 运行参数
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String operatingParameters;

    /**
     * 所用零件信息
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String partsInfo;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

