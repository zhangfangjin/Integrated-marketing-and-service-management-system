package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

/**
 * 售后维护计划实体类
 * 支持按设备类型或具体设备制定维护计划
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "maintenance_plan")
public class MaintenancePlan extends BaseEntity {

    /**
     * 计划类型（设备类型/具体设备）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    /**
     * 关联的设备类型（当planType为DEVICE_TYPE时使用）
     */
    @Column(nullable = true)
    private String deviceType; // 设备类型代码或名称

    /**
     * 关联的具体设备（当planType为SPECIFIC_DEVICE时使用）
     */
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = true)
    private Device device;

    /**
     * 设备编号（冗余字段）
     */
    @Column(nullable = true)
    private String deviceNumber;

    /**
     * 设备名称
     */
    @Column(nullable = true)
    private String deviceName;

    /**
     * 维护周期
     */
    @Column(nullable = true)
    private String maintenanceCycle;

    /**
     * 维护项目
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String maintenanceItems;

    /**
     * 注意事项
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String precautions;

    /**
     * 备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 是否有售后计划（冗余字段，方便查询）
     */
    @Column(nullable = false)
    private Boolean hasMaintenancePlan = true;
}

