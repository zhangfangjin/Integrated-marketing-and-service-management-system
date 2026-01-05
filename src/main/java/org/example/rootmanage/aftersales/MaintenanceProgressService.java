package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.MaintenanceProgressRequest;
import org.example.rootmanage.aftersales.entity.MaintenanceProgress;
import org.example.rootmanage.aftersales.repository.MaintenanceProgressRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 维修进度服务类
 * 提供维修进度查询和管理的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class MaintenanceProgressService {

    private final MaintenanceProgressRepository maintenanceProgressRepository;
    private final ContractRepository contractRepository;

    /**
     * 查询所有维修进度列表
     */
    @Transactional(readOnly = true)
    public List<MaintenanceProgress> findAll() {
        return maintenanceProgressRepository.findAll();
    }

    /**
     * 根据关键词搜索维修进度
     */
    @Transactional(readOnly = true)
    public List<MaintenanceProgress> searchMaintenanceProgress(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return maintenanceProgressRepository.searchMaintenanceProgress(keyword.trim());
        }
        return maintenanceProgressRepository.findAll();
    }

    /**
     * 根据合同ID查询维修进度
     */
    @Transactional(readOnly = true)
    public MaintenanceProgress findByContractId(UUID contractId) {
        return maintenanceProgressRepository.findByContractId(contractId)
                .orElse(null);
    }

    /**
     * 根据ID查找维修进度
     */
    @Transactional(readOnly = true)
    public MaintenanceProgress findById(UUID id) {
        return maintenanceProgressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维修进度不存在"));
    }

    /**
     * 创建或更新维修进度
     */
    @Transactional
    public MaintenanceProgress save(MaintenanceProgressRequest request) {
        Contract contract = null;
        if (request.getContractId() != null) {
            contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
        } else if (request.getContractNumber() != null) {
            contract = contractRepository.findByContractNumber(request.getContractNumber())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
        } else {
            throw new IllegalArgumentException("合同ID或合同编号不能为空");
        }

        // 查找是否已存在该合同的维修进度
        MaintenanceProgress progress = maintenanceProgressRepository.findByContractId(contract.getId())
                .orElse(new MaintenanceProgress());

        progress.setContract(contract);
        progress.setContractNumber(contract.getContractNumber());
        progress.setContractName(contract.getContractName());
        progress.setContractType(request.getContractType());
        progress.setReplacementPartsListAttachment(request.getReplacementPartsListAttachment());
        progress.setDisassemblyReportAttachment(request.getDisassemblyReportAttachment());
        progress.setPlanAttachment(request.getPlanAttachment());
        progress.setDeductionTime(request.getDeductionTime());
        progress.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        progress.setRemark(request.getRemark());

        return maintenanceProgressRepository.save(progress);
    }

    /**
     * 删除维修进度
     */
    @Transactional
    public void delete(UUID id) {
        MaintenanceProgress progress = maintenanceProgressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维修进度不存在"));
        maintenanceProgressRepository.delete(progress);
    }
}





