package org.example.rootmanage.aftersales.dto;

import lombok.Data;

import java.sql.Date;
import java.util.UUID;

/**
 * 设备维护记录创建/更新请求DTO
 */
@Data
public class DeviceMaintenanceRecordRequest {

    /**
     * 设备ID
     */
    private UUID deviceId;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 维护日期
     */
    private Date maintenanceDate;

    /**
     * 故障原因
     */
    private String faultReason;

    /**
     * 解决方案
     */
    private String solution;

    /**
     * 零件更换情况
     */
    private String partsReplacement;

    /**
     * 是否外供零件问题
     */
    private Boolean isExternalPartsIssue;

    /**
     * 外供设备厂家
     */
    private String externalSupplier;

    /**
     * 外供设备型号
     */
    private String externalDeviceModel;

    /**
     * 维护人员ID
     */
    private UUID maintenancePersonId;

    /**
     * 维护人员姓名
     */
    private String maintenancePersonName;

    /**
     * 备注
     */
    private String remark;
}

















