package org.example.rootmanage.aftersales.dto;

import lombok.Data;

import java.sql.Date;
import java.util.UUID;

/**
 * 售后经验创建/更新请求DTO
 */
@Data
public class AfterSalesExperienceRequest {

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 案例登记时间
     */
    private Date caseRegistrationDate;

    /**
     * 案例类型
     */
    private String caseType;

    /**
     * 经验总结
     */
    private String experienceSummary;

    /**
     * 案例备注
     */
    private String caseRemark;

    /**
     * 附件路径
     */
    private String attachment;

    /**
     * 登记人ID
     */
    private UUID registrantId;

    /**
     * 登记人姓名
     */
    private String registrantName;
}

















