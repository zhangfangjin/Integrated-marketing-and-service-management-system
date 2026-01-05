package org.example.rootmanage.aftersales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * 设备创建/更新请求DTO
 */
@Data
public class DeviceRequest {

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空")
    private String deviceNumber;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    /**
     * 合同ID
     */
    private UUID contractId;

    /**
     * 合同编号
     */
    private String contractNumber;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 出厂时间（生产时间）
     */
    private java.sql.Date productionDate;

    /**
     * 运行参数
     */
    private String operatingParameters;

    /**
     * 所用零件信息
     */
    private String partsInfo;

    /**
     * 备注
     */
    private String remark;
}

