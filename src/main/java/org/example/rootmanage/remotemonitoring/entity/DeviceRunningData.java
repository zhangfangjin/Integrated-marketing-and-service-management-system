package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 设备运行数据实体类
 * 用于存储采集的设备运行数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_device_running_data",
        indexes = {
                @Index(name = "idx_running_data_point_time", columnList = "dataPointId, collectionTime"),
                @Index(name = "idx_running_data_time", columnList = "collectionTime")
        })
public class DeviceRunningData extends BaseEntity {

    /**
     * 关联点位ID
     */
    @Column(name = "data_point_id", nullable = false)
    private UUID dataPointId;

    /**
     * 关联点位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_point_id", insertable = false, updatable = false)
    private DataPoint dataPoint;

    /**
     * 采集时间
     */
    @Column(nullable = false)
    private LocalDateTime collectionTime;

    /**
     * 采集值
     */
    @Column
    private Double value;

    /**
     * 原始值（未经倍率转换的值）
     */
    @Column
    private Double rawValue;

    /**
     * 数据质量（GOOD-良好, BAD-坏数据, UNCERTAIN-不确定）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataQuality quality = DataQuality.GOOD;

    /**
     * 数据来源（AUTO-自动采集, MANUAL-手动录入）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataSource source = DataSource.AUTO;

    /**
     * 手动录入人ID
     */
    @Column
    private UUID inputById;

    /**
     * 手动录入人姓名
     */
    @Column
    private String inputByName;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}




