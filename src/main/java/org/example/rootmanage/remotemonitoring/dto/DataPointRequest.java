package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.CollectionMode;
import org.example.rootmanage.remotemonitoring.entity.MeasurementType;
import org.example.rootmanage.remotemonitoring.entity.PointType;

import java.util.UUID;

/**
 * 数据点位请求DTO
 */
@Data
public class DataPointRequest {

    /**
     * 点位编码
     */
    @NotBlank(message = "点位编码不能为空")
    private String pointCode;

    /**
     * 点位名称
     */
    @NotBlank(message = "点位名称不能为空")
    private String pointName;

    /**
     * 点位类型
     */
    private PointType pointType;

    /**
     * 数据类型
     */
    private MeasurementType dataType;

    /**
     * 关联表计ID
     */
    private UUID meterId;

    /**
     * 关联检测属性ID
     */
    private UUID measurementId;

    /**
     * 单位
     */
    private String unit;

    /**
     * 采集倍率
     */
    private Double multiplier;

    /**
     * 采集模式
     */
    private CollectionMode collectionMode;

    /**
     * 采集周期（秒）
     */
    private Integer collectionInterval;

    /**
     * 数据精度
     */
    private Integer precision;

    /**
     * 最小值
     */
    private Double minValue;

    /**
     * 最大值
     */
    private Double maxValue;

    /**
     * 通信协议
     */
    private String protocol;

    /**
     * 通信地址
     */
    private String commAddress;

    /**
     * 寄存器地址
     */
    private String registerAddress;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否报警点位
     */
    private Boolean alarmEnabled;

    /**
     * 备注
     */
    private String remark;
}

















