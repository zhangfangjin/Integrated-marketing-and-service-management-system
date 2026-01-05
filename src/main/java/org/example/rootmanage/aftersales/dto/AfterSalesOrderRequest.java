package org.example.rootmanage.aftersales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.rootmanage.aftersales.entity.AfterSalesType;

import java.sql.Date;
import java.util.UUID;

/**
 * 售后服务单创建/更新请求DTO
 */
@Data
public class AfterSalesOrderRequest {

    /**
     * 服务单号（创建时可选，系统可自动生成）
     */
    private String serviceOrderNumber;

    /**
     * 售后类型
     */
    @NotNull(message = "售后类型不能为空")
    private AfterSalesType serviceType;

    /**
     * 关联的合同ID
     */
    private UUID contractId;

    /**
     * 合同编号
     */
    private String contractNumber;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 业主单位（客户单位）
     */
    private String customerUnit;

    /**
     * 付款计划
     */
    private String paymentPlan;

    /**
     * 售后时间
     */
    private Date serviceDate;

    /**
     * 地区
     */
    private String region;

    /**
     * 维修方案附件路径
     */
    private String maintenancePlanAttachment;

    /**
     * 换件清单附件路径
     */
    private String replacementPartsListAttachment;

    /**
     * 客户请求附件路径
     */
    private String customerRequestAttachment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 受理人员ID（指派处理人员）
     */
    private UUID handlerId;

    /**
     * 受理人员姓名
     */
    private String handlerName;

    /**
     * 服务状态
     */
    private String serviceStatus;

    /**
     * 完成日期
     */
    private Date completionDate;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 售后总结
     */
    private String serviceSummary;
}

