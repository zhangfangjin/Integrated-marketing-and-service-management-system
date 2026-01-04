package org.example.rootmanage.receivable.dto;

import lombok.Data;
import org.example.rootmanage.contract.entity.ContractPaymentStage;

import java.sql.Date;
import java.util.UUID;

/**
 * 应收账响应DTO
 * 用于应收账查询和展示
 */
@Data
public class AccountsReceivableResponse {
    private UUID id;
    private String contractNumber;
    private String contractName;
    private String customerName;
    private String paymentStage;
    private Double amountPayable;
    private Double amountPaid;
    private Double unpaidAmount; // 未付金额
    private String paymentStageName;
    private Date dueDate;
    private Date paymentDate;
    private String responsiblePerson;
    private String remark;

    /**
     * 从ContractPaymentStage转换
     */
    public static AccountsReceivableResponse from(ContractPaymentStage stage) {
        AccountsReceivableResponse response = new AccountsReceivableResponse();
        response.setId(stage.getId());
        response.setContractNumber(stage.getContract().getContractNumber());
        response.setContractName(stage.getContract().getContractName());
        response.setCustomerName(stage.getContract().getCustomerName());
        response.setPaymentStage(stage.getPaymentStage());
        response.setAmountPayable(stage.getAmountPayable());
        response.setAmountPaid(stage.getAmountPaid() != null ? stage.getAmountPaid() : 0.0);
        response.setUnpaidAmount(
            (stage.getAmountPayable() != null ? stage.getAmountPayable() : 0.0) - 
            (stage.getAmountPaid() != null ? stage.getAmountPaid() : 0.0)
        );
        response.setPaymentStageName(stage.getPaymentStageName());
        response.setDueDate(stage.getDueDate());
        response.setPaymentDate(stage.getPaymentDate());
        response.setResponsiblePerson(stage.getResponsiblePerson());
        response.setRemark(stage.getRemark());
        return response;
    }
}

