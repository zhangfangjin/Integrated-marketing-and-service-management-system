package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.sql.Date;

/**
 * 设备维护记录实体类
 * 用于记录设备每次售后维护的详细信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "device_maintenance_record")
public class DeviceMaintenanceRecord extends BaseEntity {

    /**
     * 关联的设备
     */
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    /**
     * 设备编号（冗余字段）
     */
    @Column(nullable = true)
    private String deviceNumber;

    /**
     * 维护日期
     */
    @Column(nullable = true)
    private Date maintenanceDate;

    /**
     * 故障原因
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String faultReason;

    /**
     * 解决方案
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String solution;

    /**
     * 零件更换情况
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String partsReplacement;

    /**
     * 是否外供零件问题
     */
    @Column(nullable = false)
    private Boolean isExternalPartsIssue = false;

    /**
     * 外供设备厂家
     */
    @Column(nullable = true)
    private String externalSupplier;

    /**
     * 外供设备型号
     */
    @Column(nullable = true)
    private String externalDeviceModel;

    /**
     * 维护人员ID
     */
    @Column(nullable = true)
    private java.util.UUID maintenancePersonId;

    /**
     * 维护人员姓名
     */
    @Column(nullable = true)
    private String maintenancePersonName;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;
}

















