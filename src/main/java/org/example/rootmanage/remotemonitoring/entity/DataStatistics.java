package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 数据统计实体类
 * 用于存储按小时、天、周、月等周期统计的数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_data_statistics",
        indexes = {
                @Index(name = "idx_statistics_point_period", columnList = "dataPointId, statisticsPeriod, statisticsDate"),
                @Index(name = "idx_statistics_date", columnList = "statisticsDate")
        })
public class DataStatistics extends BaseEntity {

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
     * 统计周期类型（HOURLY-小时, DAILY-天, WEEKLY-周, MONTHLY-月）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatisticsPeriod statisticsPeriod;

    /**
     * 统计日期
     */
    @Column(nullable = false)
    private LocalDate statisticsDate;

    /**
     * 统计时段（针对小时统计，0-23）
     */
    @Column
    private Integer hourOfDay;

    /**
     * 周（针对周统计，1-52）
     */
    @Column
    private Integer weekOfYear;

    /**
     * 统计开始时间
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * 统计结束时间
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * 平均值
     */
    @Column
    private Double avgValue;

    /**
     * 最大值
     */
    @Column
    private Double maxValue;

    /**
     * 最大值发生时间
     */
    @Column
    private LocalDateTime maxValueTime;

    /**
     * 最小值
     */
    @Column
    private Double minValue;

    /**
     * 最小值发生时间
     */
    @Column
    private LocalDateTime minValueTime;

    /**
     * 累计值（针对累计量）
     */
    @Column
    private Double sumValue;

    /**
     * 数据点数
     */
    @Column
    private Integer dataCount;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}




