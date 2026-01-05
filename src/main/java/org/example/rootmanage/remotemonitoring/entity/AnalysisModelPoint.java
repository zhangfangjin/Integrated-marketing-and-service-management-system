package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 分析模型点位关联实体类
 * 用于将数据点位与分析模型进行关联
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_analysis_model_point")
public class AnalysisModelPoint extends BaseEntity {

    /**
     * 所属分析模型ID
     */
    @Column(name = "analysis_model_id", nullable = false)
    private UUID analysisModelId;

    /**
     * 所属分析模型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_model_id", insertable = false, updatable = false)
    private AnalysisModel analysisModel;

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
     * 点位显示名称（可自定义）
     */
    @Column
    private String displayName;

    /**
     * 曲线分析分组（如：电流、压力、振动频率等）
     */
    @Column
    private String curveGroup;

    /**
     * 曲线颜色
     */
    @Column
    private String curveColor;

    /**
     * Y轴最小值
     */
    @Column
    private Double yAxisMin;

    /**
     * Y轴最大值
     */
    @Column
    private Double yAxisMax;

    /**
     * 排序序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}




