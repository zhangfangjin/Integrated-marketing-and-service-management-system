package org.example.rootmanage.contract.dto;

import lombok.Data;

/**
 * 合同细目请求DTO
 */
@Data
public class ContractDetailRequest {

    /**
     * 类型
     */
    private String type;

    /**
     * 产品型号
     */
    private String productModel;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 细分类型
     */
    private String subType;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 备注
     */
    private String remark;
}

