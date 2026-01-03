package org.example.rootmanage.contract.dto;

import lombok.Data;

/**
 * 合同执行进度请求DTO
 */
@Data
public class ContractExecutionProgressRequest {

    /**
     * 设计进度（已完成、未完成、进行中等）
     */
    private String designProgress;

    /**
     * 生产进度（已完成、未完成、进行中等）
     */
    private String productionProgress;

    /**
     * 采购进度（已完成、未完成、进行中等）
     */
    private String procurementProgress;

    /**
     * 制造进度（已完成、未完成、进行中等）
     */
    private String manufacturingProgress;

    /**
     * 装配进度（已完成、未完成、进行中等）
     */
    private String assemblyProgress;

    /**
     * 备注
     */
    private String remark;
}

