package org.example.rootmanage.receivable;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.contract.entity.ContractPaymentStage;
import org.example.rootmanage.contract.repository.ContractPaymentStageRepository;
import org.example.rootmanage.receivable.dto.AccountsReceivableResponse;
import org.example.rootmanage.receivable.dto.AccountsReceivableUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应收账管理Service
 */
@Service
@RequiredArgsConstructor
public class AccountsReceivableService {

    private final ContractPaymentStageRepository contractPaymentStageRepository;

    /**
     * 获取所有应收账计划（所有合同付款阶段）
     */
    public List<AccountsReceivableResponse> findAll() {
        return contractPaymentStageRepository.findAll().stream()
                .map(AccountsReceivableResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据合同编号查询应收账
     */
    public List<AccountsReceivableResponse> findByContractNumber(String contractNumber) {
        return contractPaymentStageRepository.findByContractNumber(contractNumber).stream()
                .map(AccountsReceivableResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据关键词搜索应收账（合同编号、合同名称、客户名称）
     */
    public List<AccountsReceivableResponse> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return contractPaymentStageRepository.searchByKeyword("%" + keyword.trim() + "%").stream()
                .map(AccountsReceivableResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取应收账详情
     */
    public AccountsReceivableResponse findById(UUID id) {
        ContractPaymentStage stage = contractPaymentStageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("应收账记录不存在"));
        return AccountsReceivableResponse.from(stage);
    }

    /**
     * 更新应收账信息（应收账录入/修改）
     */
    @Transactional
    public AccountsReceivableResponse update(UUID id, AccountsReceivableUpdateRequest request) {
        ContractPaymentStage stage = contractPaymentStageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("应收账记录不存在"));

        if (request.getPaymentStage() != null) {
            stage.setPaymentStage(request.getPaymentStage());
        }
        if (request.getAmountPayable() != null) {
            stage.setAmountPayable(request.getAmountPayable());
        }
        if (request.getAmountPaid() != null) {
            stage.setAmountPaid(request.getAmountPaid());
        }
        if (request.getPaymentStageName() != null) {
            stage.setPaymentStageName(request.getPaymentStageName());
        }
        if (request.getDueDate() != null) {
            stage.setDueDate(request.getDueDate());
        }
        if (request.getPaymentDate() != null) {
            stage.setPaymentDate(request.getPaymentDate());
        }
        if (request.getResponsiblePerson() != null) {
            stage.setResponsiblePerson(request.getResponsiblePerson());
        }
        if (request.getRemark() != null) {
            stage.setRemark(request.getRemark());
        }

        ContractPaymentStage saved = contractPaymentStageRepository.save(stage);
        return AccountsReceivableResponse.from(saved);
    }

    /**
     * 获取未付清的应收账（已付金额小于应付金额）
     */
    public List<AccountsReceivableResponse> findUnpaid() {
        return contractPaymentStageRepository.findAll().stream()
                .filter(stage -> {
                    Double payable = stage.getAmountPayable() != null ? stage.getAmountPayable() : 0.0;
                    Double paid = stage.getAmountPaid() != null ? stage.getAmountPaid() : 0.0;
                    return payable > paid;
                })
                .map(AccountsReceivableResponse::from)
                .collect(Collectors.toList());
    }
}

