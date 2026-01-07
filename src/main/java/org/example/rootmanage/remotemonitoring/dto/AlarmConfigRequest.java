package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.AlarmLevel;
import org.example.rootmanage.remotemonitoring.entity.AlarmType;

import java.util.UUID;

/**
 * 报警配置请求DTO
 */
@Data
public class AlarmConfigRequest {

    /**
     * 报警配置编码
     */
    @NotBlank(message = "报警配置编码不能为空")
    private String alarmCode;

    /**
     * 报警配置名称
     */
    @NotBlank(message = "报警配置名称不能为空")
    private String alarmName;

    /**
     * 关联点位ID
     */
    @NotNull(message = "关联点位ID不能为空")
    private UUID dataPointId;

    /**
     * 报警类型
     */
    private AlarmType alarmType;

    /**
     * 报警级别
     */
    private AlarmLevel alarmLevel;

    /**
     * 上限值
     */
    private Double upperLimit;

    /**
     * 下限值
     */
    private Double lowerLimit;

    /**
     * 死区值
     */
    private Double deadband;

    /**
     * 延迟时间（秒）
     */
    private Integer delaySeconds;

    /**
     * 报警描述模板
     */
    private String alarmMessageTemplate;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否发送通知
     */
    private Boolean notifyEnabled;

    /**
     * 通知方式
     */
    private String notifyMethod;

    /**
     * 通知接收人
     */
    private String notifyReceivers;

    /**
     * 备注
     */
    private String remark;
}

















