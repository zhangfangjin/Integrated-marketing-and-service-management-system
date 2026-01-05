package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 设备模型请求DTO
 */
@Data
public class DeviceModelRequest {

    /**
     * 设备模型编码
     */
    @NotBlank(message = "设备模型编码不能为空")
    private String modelCode;

    /**
     * 设备模型名称
     */
    @NotBlank(message = "设备模型名称不能为空")
    private String modelName;

    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;

    /**
     * 设备品牌
     */
    private String brand;

    /**
     * 设备规格
     */
    private String specification;

    /**
     * 额定功率
     */
    private String ratedPower;

    /**
     * 额定电压
     */
    private String ratedVoltage;

    /**
     * 额定电流
     */
    private String ratedCurrent;

    /**
     * 额定流量
     */
    private String ratedFlow;

    /**
     * 额定扬程
     */
    private String ratedHead;

    /**
     * 设备模型属性列表
     */
    private List<DeviceModelAttributeRequest> attributes;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}





