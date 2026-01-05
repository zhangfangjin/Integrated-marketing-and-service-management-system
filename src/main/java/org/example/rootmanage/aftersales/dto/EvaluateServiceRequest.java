package org.example.rootmanage.aftersales.dto;

import lombok.Data;

/**
 * 评价售后服务请求DTO
 */
@Data
public class EvaluateServiceRequest {

    /**
     * 售后评价（优、良、中、差等）
     */
    private String evaluation;

    /**
     * 备注
     */
    private String remark;

    /**
     * 评价人ID
     */
    private java.util.UUID evaluatorId;

    /**
     * 评价人姓名
     */
    private String evaluatorName;
}





