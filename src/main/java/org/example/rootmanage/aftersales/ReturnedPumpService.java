package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.ReturnedPumpRequest;
import org.example.rootmanage.aftersales.entity.ReturnedPump;
import org.example.rootmanage.aftersales.repository.ReturnedPumpRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 返厂泵服务类
 * 提供返厂泵信息查询和管理的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ReturnedPumpService {

    private final ReturnedPumpRepository returnedPumpRepository;
    private final ContractRepository contractRepository;

    /**
     * 查询所有返厂泵列表
     */
    @Transactional(readOnly = true)
    public List<ReturnedPump> findAll() {
        return returnedPumpRepository.findAll();
    }

    /**
     * 根据关键词搜索返厂泵
     */
    @Transactional(readOnly = true)
    public List<ReturnedPump> searchReturnedPumps(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return returnedPumpRepository.searchReturnedPumps(keyword.trim());
        }
        return returnedPumpRepository.findAll();
    }

    /**
     * 根据合同ID查询返厂泵列表
     */
    @Transactional(readOnly = true)
    public List<ReturnedPump> findByContractId(UUID contractId) {
        return returnedPumpRepository.findByContractId(contractId);
    }

    /**
     * 根据ID查找返厂泵
     */
    @Transactional(readOnly = true)
    public ReturnedPump findById(UUID id) {
        return returnedPumpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("返厂泵信息不存在"));
    }

    /**
     * 创建返厂泵信息
     */
    @Transactional
    public ReturnedPump create(ReturnedPumpRequest request) {
        ReturnedPump pump = new ReturnedPump();
        pump.setPumpName(request.getPumpName());
        pump.setReturnDate(request.getReturnDate());
        pump.setPumpModel(request.getPumpModel());
        pump.setRemark(request.getRemark());

        // 设置合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            pump.setContract(contract);
            pump.setContractNumber(contract.getContractNumber());
            pump.setContractName(contract.getContractName());
        } else if (request.getContractNumber() != null) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        pump.setContract(contract);
                        pump.setContractNumber(contract.getContractNumber());
                        pump.setContractName(contract.getContractName());
                    });
        } else {
            // 如果没有提供合同ID或编号，直接使用提供的合同信息
            pump.setContractNumber(request.getContractNumber());
            pump.setContractName(request.getContractName());
        }

        return returnedPumpRepository.save(pump);
    }

    /**
     * 更新返厂泵信息
     */
    @Transactional
    public ReturnedPump update(UUID id, ReturnedPumpRequest request) {
        ReturnedPump pump = returnedPumpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("返厂泵信息不存在"));

        pump.setPumpName(request.getPumpName());
        pump.setReturnDate(request.getReturnDate());
        pump.setPumpModel(request.getPumpModel());
        pump.setRemark(request.getRemark());

        // 更新合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            pump.setContract(contract);
            pump.setContractNumber(contract.getContractNumber());
            pump.setContractName(contract.getContractName());
        } else if (request.getContractNumber() != null) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        pump.setContract(contract);
                        pump.setContractNumber(contract.getContractNumber());
                        pump.setContractName(contract.getContractName());
                    });
        } else {
            pump.setContractNumber(request.getContractNumber());
            pump.setContractName(request.getContractName());
        }

        return returnedPumpRepository.save(pump);
    }

    /**
     * 删除返厂泵信息
     */
    @Transactional
    public void delete(UUID id) {
        ReturnedPump pump = returnedPumpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("返厂泵信息不存在"));
        returnedPumpRepository.delete(pump);
    }
}

















