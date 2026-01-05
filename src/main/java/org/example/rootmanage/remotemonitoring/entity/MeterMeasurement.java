package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 表计检测属性配置实体类
 * 用于配置表计的检测基础属性库，如水表的流量、压力、温度，电表的电压、电流、功率等
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_meter_measurement")
public class MeterMeasurement extends BaseEntity {

    /**
     * 所属表计ID
     */
    @Column(name = "meter_id", nullable = false)
    private UUID meterId;

    /**
     * 所属表计
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", insertable = false, updatable = false)
    private Meter meter;

    /**
     * 检测属性名称
     */
    @Column(nullable = false)
    private String measurementName;

    /**
     * 检测属性编码
     */
    @Column(nullable = false)
    private String measurementCode;

    /**
     * 检测属性类型（ANALOG-模拟量, DIGITAL-数字量, CUMULATIVE-累计量）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementType measurementType = MeasurementType.ANALOG;

    /**
     * 单位
     */
    @Column
    private String unit;

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
     * 关联点位ID
     */
    @Column
    private UUID dataPointId;

    /**
     * 排序序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}





