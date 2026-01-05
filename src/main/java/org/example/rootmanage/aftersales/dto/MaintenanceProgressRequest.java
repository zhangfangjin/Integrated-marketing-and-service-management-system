package org.example.rootmanage.aftersales.dto;

import lombok.Data;

import java.sql.Date;
import java.util.UUID;

/**
 * 维修进度创建/更新请求DTO
 */
@Data
public class MaintenanceProgressRequest {

    /**
     * 合同ID
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
     * 合同类型
     */
    private String contractType;

    /**
     * 换件清单附件路径
     */
    private String replacementPartsListAttachment;

    /**
     * 拆解报告附件路径
     */
    private String disassemblyReportAttachment;

    /**
     * 计划附件路径
     */
    private String planAttachment;

    /**
     * 扣套时间
     */
    private Date deductionTime;

    /**
     * 预计发货时间
     */
    private Date estimatedDeliveryTime;

    /**
     * 备注
     */
    private String remark;
}





