package org.example.rootmanage.contract.dto;

import lombok.Data;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractExecutionProgress;

import java.util.UUID;

/**
 * 合同执行进度响应DTO
 * 用于合同执行动态列表展示
 */
@Data
public class ContractExecutionProgressResponse {

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
     * 设计进度
     */
    private String designProgress;

    /**
     * 生产进度
     */
    private String productionProgress;

    /**
     * 采购进度
     */
    private String procurementProgress;

    /**
     * 制造进度
     */
    private String manufacturingProgress;

    /**
     * 装配进度
     */
    private String assemblyProgress;

    /**
     * 从实体类转换为响应DTO
     */
    public static ContractExecutionProgressResponse fromEntity(ContractExecutionProgress progress) {
        ContractExecutionProgressResponse response = new ContractExecutionProgressResponse();
        if (progress != null && progress.getContract() != null) {
            Contract contract = progress.getContract();
            response.setContractId(contract.getId());
            response.setContractNumber(contract.getContractNumber());
            response.setContractName(contract.getContractName());
            response.setDesignProgress(progress.getDesignProgress());
            response.setProductionProgress(progress.getProductionProgress());
            response.setProcurementProgress(progress.getProcurementProgress());
            response.setManufacturingProgress(progress.getManufacturingProgress());
            response.setAssemblyProgress(progress.getAssemblyProgress());
        }
        return response;
    }
}

