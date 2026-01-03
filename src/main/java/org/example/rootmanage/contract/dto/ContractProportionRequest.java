package org.example.rootmanage.contract.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 合同占比划分请求DTO
 */
@Data
public class ContractProportionRequest {

    /**
     * 负责人ID
     */
    private UUID personInChargeId;

    /**
     * 负责人姓名
     */
    private String personInChargeName;

    /**
     * 所属片区
     */
    private String affiliatedArea;

    /**
     * 占比划分（百分比）
     */
    private Double proportion;

    /**
     * 备注
     */
    private String remark;
}

