package org.example.rootmanage.contract.dto;

import lombok.Data;

import java.sql.Date;

/**
 * 合同付款阶段请求DTO
 */
@Data
public class ContractPaymentStageRequest {

    /**
     * 付款阶段
     */
    private String paymentStage;

    /**
     * 应付金额
     */
    private Double amountPayable;

    /**
     * 已付金额
     */
    private Double amountPaid;

    /**
     * 付款阶段名称
     */
    private String paymentStageName;

    /**
     * 付款日期
     */
    private Date paymentDate;

    /**
     * 备注
     */
    private String remark;
}

