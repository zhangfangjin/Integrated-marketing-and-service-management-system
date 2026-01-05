package org.example.rootmanage.aftersales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;

/**
 * 售后服务活动创建请求DTO
 */
@Data
public class AfterSalesActivityRequest {

    /**
     * 售后服务单ID
     */
    private UUID afterSalesOrderId;

    /**
     * 活动类型（跟踪、完成、评价等）
     */
    @NotBlank(message = "活动类型不能为空")
    private String activityType;

    /**
     * 活动时间
     */
    private Date activityDate;

    /**
     * 说明（情况说明）
     */
    private String description;

    /**
     * 操作人ID
     */
    private UUID operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;
}





