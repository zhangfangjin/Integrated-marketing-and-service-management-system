package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 数据点位实体类
 * 用于配置采集点位数据，实现点位绑定和数据采集管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_data_point")
public class DataPoint extends BaseEntity {

    /**
     * 点位编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String pointCode;

    /**
     * 点位名称
     */
    @Column(nullable = false)
    private String pointName;

    /**
     * 点位类型（REAL-实点, VIRTUAL-虚点）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PointType pointType = PointType.REAL;

    /**
     * 数据类型（ANALOG-模拟量, DIGITAL-数字量, CUMULATIVE-累计量）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementType dataType = MeasurementType.ANALOG;

    /**
     * 关联表计ID
     */
    @Column(name = "meter_id")
    private UUID meterId;

    /**
     * 关联表计
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", insertable = false, updatable = false)
    private Meter meter;

    /**
     * 关联检测属性ID
     */
    @Column
    private UUID measurementId;

    /**
     * 单位
     */
    @Column
    private String unit;

    /**
     * 采集倍率
     */
    @Column
    private Double multiplier = 1.0;

    /**
     * 采集模式（REALTIME-实时采集, POLLING-轮询采集, MANUAL-手动录入）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CollectionMode collectionMode = CollectionMode.POLLING;

    /**
     * 采集周期（秒）
     */
    @Column
    private Integer collectionInterval = 60;

    /**
     * 数据精度（小数位数）
     */
    @Column
    private Integer precision = 2;

    /**
     * 最小值
     */
    @Column
    private Double minValue;

    /**
     * 最大值
     */
    @Column
    private Double maxValue;

    /**
     * 通信协议
     */
    @Column
    private String protocol;

    /**
     * 通信地址
     */
    @Column
    private String commAddress;

    /**
     * 寄存器地址
     */
    @Column
    private String registerAddress;

    /**
     * 当前值（最新采集值）
     */
    @Column
    private Double currentValue;

    /**
     * 最后采集时间
     */
    @Column
    private LocalDateTime lastCollectionTime;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 是否报警点位
     */
    @Column(nullable = false)
    private Boolean alarmEnabled = false;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}





