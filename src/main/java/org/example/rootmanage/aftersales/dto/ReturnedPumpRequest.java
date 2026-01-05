package org.example.rootmanage.aftersales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;

/**
 * 返厂泵创建/更新请求DTO
 */
@Data
public class ReturnedPumpRequest {

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
     * 返厂泵名称
     */
    @NotBlank(message = "返厂泵名称不能为空")
    private String pumpName;

    /**
     * 返厂时间
     */
    private Date returnDate;

    /**
     * 泵型号
     */
    private String pumpModel;

    /**
     * 备注
     */
    private String remark;
}





