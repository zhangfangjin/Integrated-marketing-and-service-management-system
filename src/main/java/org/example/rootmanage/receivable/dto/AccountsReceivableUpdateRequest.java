package org.example.rootmanage.receivable.dto;

import lombok.Data;

import java.sql.Date;

/**
 * 应收账更新请求DTO
 * 用于应收账录入和修改
 */
@Data
public class AccountsReceivableUpdateRequest {
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
     * 应付时间
     */
    private Date dueDate;

    /**
     * 付款日期
     */
    private Date paymentDate;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 备注
     */
    private String remark;
}

