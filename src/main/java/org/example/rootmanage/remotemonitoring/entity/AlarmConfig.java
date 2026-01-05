package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 报警配置实体类
 * 用于配置点位的报警阈值和报警规则
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_alarm_config")
public class AlarmConfig extends BaseEntity {

    /**
     * 报警配置编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String alarmCode;

    /**
     * 报警配置名称
     */
    @Column(nullable = false)
    private String alarmName;

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
     * 报警类型（HIGH-超上限, LOW-超下限, CHANGE-变化率, STATUS-状态变化）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    /**
     * 报警级别（INFO-信息, WARNING-警告, ERROR-错误, CRITICAL-严重）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmLevel alarmLevel = AlarmLevel.WARNING;

    /**
     * 上限值
     */
    @Column
    private Double upperLimit;

    /**
     * 下限值
     */
    @Column
    private Double lowerLimit;

    /**
     * 死区值（防止频繁报警）
     */
    @Column
    private Double deadband = 0.0;

    /**
     * 延迟时间（秒，超过阈值多久后才报警）
     */
    @Column
    private Integer delaySeconds = 0;

    /**
     * 报警描述模板
     */
    @Column(columnDefinition = "TEXT")
    private String alarmMessageTemplate;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 是否发送通知
     */
    @Column(nullable = false)
    private Boolean notifyEnabled = true;

    /**
     * 通知方式（SMS-短信, EMAIL-邮件, SYSTEM-系统通知）
     */
    @Column
    private String notifyMethod;

    /**
     * 通知接收人（多个用逗号分隔）
     */
    @Column(columnDefinition = "TEXT")
    private String notifyReceivers;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}




