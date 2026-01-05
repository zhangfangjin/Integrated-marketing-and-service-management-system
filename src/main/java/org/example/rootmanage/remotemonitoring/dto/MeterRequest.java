package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.MeterType;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

/**
 * 表计请求DTO
 */
@Data
public class MeterRequest {

    /**
     * 表计编号
     */
    @NotBlank(message = "表计编号不能为空")
    private String meterCode;

    /**
     * 表计名称
     */
    @NotBlank(message = "表计名称不能为空")
    private String meterName;

    /**
     * 表计类型
     */
    private MeterType meterType;

    /**
     * 表计功能描述
     */
    private String meterFunction;

    /**
     * 表计型号
     */
    private String meterModel;

    /**
     * 表计单位
     */
    private String meterUnit;

    /**
     * 表计倍率
     */
    private Double multiplier;

    /**
     * 安装位置
     */
    private String installLocation;

    /**
     * 安装日期
     */
    private Date installDate;

    /**
     * 所属空间节点ID
     */
    private UUID spaceNodeId;

    /**
     * 关联设备ID
     */
    private UUID deviceId;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 通信协议
     */
    private String protocol;

    /**
     * 通信地址
     */
    private String commAddress;

    /**
     * 静态属性列表
     */
    private List<MeterAttributeRequest> staticAttributes;

    /**
     * 检测属性列表
     */
    private List<MeterMeasurementRequest> measurements;

    /**
     * 备注
     */
    private String remark;
}





