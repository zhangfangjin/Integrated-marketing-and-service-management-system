package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 报警记录实体类
 * 用于记录报警发生的历史信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_alarm_record")
public class AlarmRecord extends BaseEntity {

    /**
     * 关联报警配置ID
     */
    @Column(name = "alarm_config_id", nullable = false)
    private UUID alarmConfigId;

    /**
     * 关联报警配置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_config_id", insertable = false, updatable = false)
    private AlarmConfig alarmConfig;

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
     * 报警类型
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    /**
     * 报警级别
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmLevel alarmLevel;

    /**
     * 报警发生时间
     */
    @Column(nullable = false)
    private LocalDateTime alarmTime;

    /**
     * 报警恢复时间
     */
    @Column
    private LocalDateTime recoveryTime;

    /**
     * 报警时的数值
     */
    @Column
    private Double alarmValue;

    /**
     * 报警阈值
     */
    @Column
    private Double thresholdValue;

    /**
     * 报警消息
     */
    @Column(columnDefinition = "TEXT")
    private String alarmMessage;

    /**
     * 报警状态（ACTIVE-活动, ACKNOWLEDGED-已确认, RECOVERED-已恢复）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmStatus status = AlarmStatus.ACTIVE;

    /**
     * 确认人ID
     */
    @Column
    private UUID acknowledgedById;

    /**
     * 确认人姓名
     */
    @Column
    private String acknowledgedByName;

    /**
     * 确认时间
     */
    @Column
    private LocalDateTime acknowledgedTime;

    /**
     * 处理备注
     */
    @Column(columnDefinition = "TEXT")
    private String handleRemark;
}




