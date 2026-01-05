package org.example.rootmanage.aftersales.dto;

import lombok.Data;
import org.example.rootmanage.aftersales.entity.PlanType;

import java.util.List;
import java.util.UUID;

/**
 * 维护计划创建/更新请求DTO
 */
@Data
public class MaintenancePlanRequest {

    /**
     * 计划类型（设备类型/具体设备）
     */
    private PlanType planType;

    /**
     * 设备类型（当planType为DEVICE_TYPE时使用）
     */
    private String deviceType;

    /**
     * 设备ID（当planType为SPECIFIC_DEVICE时使用）
     */
    private UUID deviceId;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 维护周期
     */
    private String maintenanceCycle;

    /**
     * 维护项目
     */
    private String maintenanceItems;

    /**
     * 注意事项
     */
    private String precautions;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 换件清单项列表
     */
    private List<ReplacementPartItemRequest> replacementParts;
}

