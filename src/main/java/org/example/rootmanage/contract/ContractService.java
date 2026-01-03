package org.example.rootmanage.contract;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.contract.dto.ApprovalNodeConfigRequest;
import org.example.rootmanage.contract.dto.ContractDetailRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressResponse;
import org.example.rootmanage.contract.dto.ContractPaymentStageRequest;
import org.example.rootmanage.contract.dto.ContractProportionRequest;
import org.example.rootmanage.contract.dto.ContractRequest;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractApprovalNode;
import org.example.rootmanage.contract.entity.ContractDetail;
import org.example.rootmanage.contract.entity.ContractExecutionProgress;
import org.example.rootmanage.contract.entity.ContractPaymentStage;
import org.example.rootmanage.contract.entity.ContractProportion;
import org.example.rootmanage.contract.entity.ContractWorkflowStatus;
import org.example.rootmanage.contract.repository.ContractApprovalNodeRepository;
import org.example.rootmanage.contract.repository.ContractDetailRepository;
import org.example.rootmanage.contract.repository.ContractExecutionProgressRepository;
import org.example.rootmanage.contract.repository.ContractPaymentStageRepository;
import org.example.rootmanage.contract.repository.ContractProportionRepository;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.example.rootmanage.contract.repository.ContractWorkflowStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 合同服务类
 * 提供合同管理的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractDetailRepository contractDetailRepository;
    private final ContractPaymentStageRepository contractPaymentStageRepository;
    private final ContractProportionRepository contractProportionRepository;
    private final ContractApprovalNodeRepository contractApprovalNodeRepository;
    private final ContractWorkflowStatusRepository contractWorkflowStatusRepository;
    private final ContractExecutionProgressRepository contractExecutionProgressRepository;

    /**
     * 查询所有合同列表
     */
    @Transactional(readOnly = true)
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    /**
     * 根据关键词搜索合同
     */
    @Transactional(readOnly = true)
    public List<Contract> searchContracts(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return contractRepository.searchContracts(keyword.trim());
        }
        return contractRepository.findAll();
    }

    /**
     * 根据ID查找合同
     */
    @Transactional(readOnly = true)
    public Contract findById(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
        // 初始化关联对象
        initializeContractAssociations(contract);
        return contract;
    }

    /**
     * 创建合同
     */
    @Transactional
    public Contract create(ContractRequest request) {
        // 检查合同编号是否已存在
        contractRepository.findByContractNumber(request.getContractNumber())
                .ifPresent(c -> {
                    throw new IllegalStateException("合同编号已存在");
                });

        Contract contract = new Contract();
        mapRequestToContract(request, contract);
        contract.setContractStatus("待提交");
        Contract savedContract = contractRepository.save(contract);

        // 保存合同细目
        if (request.getDetails() != null) {
            saveContractDetails(savedContract, request.getDetails());
        }

        // 保存付款阶段
        if (request.getPaymentStages() != null) {
            savePaymentStages(savedContract, request.getPaymentStages());
        }

        // 保存占比划分
        if (request.getProportions() != null) {
            saveProportions(savedContract, request.getProportions());
        }

        return savedContract;
    }

    /**
     * 更新合同
     */
    @Transactional
    public Contract update(UUID id, ContractRequest request) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));

        // 检查合同编号是否被其他合同使用
        if (!contract.getContractNumber().equals(request.getContractNumber())) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(c -> {
                        throw new IllegalStateException("合同编号已被其他合同使用");
                    });
        }

        mapRequestToContract(request, contract);
        Contract savedContract = contractRepository.save(contract);

        // 删除旧的细目、付款阶段、占比划分
        contractDetailRepository.deleteByContractId(id);
        contractPaymentStageRepository.deleteByContractId(id);
        contractProportionRepository.deleteByContractId(id);

        // 保存新的细目、付款阶段、占比划分
        if (request.getDetails() != null) {
            saveContractDetails(savedContract, request.getDetails());
        }
        if (request.getPaymentStages() != null) {
            savePaymentStages(savedContract, request.getPaymentStages());
        }
        if (request.getProportions() != null) {
            saveProportions(savedContract, request.getProportions());
        }

        return savedContract;
    }

    /**
     * 删除合同
     */
    @Transactional
    public void delete(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));

        // 删除关联数据
        contractDetailRepository.deleteByContractId(id);
        contractPaymentStageRepository.deleteByContractId(id);
        contractProportionRepository.deleteByContractId(id);
        contractApprovalNodeRepository.deleteByContractId(id);
        contractWorkflowStatusRepository.deleteByContractId(id);
        
        // 删除执行进度
        contractExecutionProgressRepository.findByContractId(id)
                .ifPresent(contractExecutionProgressRepository::delete);

        // 删除合同
        contractRepository.delete(contract);
    }

    /**
     * 保存合同（不提交审批）
     */
    @Transactional
    public Contract save(ContractRequest request) {
        if (request.getContractNumber() == null || request.getContractNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("合同编号不能为空");
        }

        Contract existingContract = contractRepository.findByContractNumber(request.getContractNumber())
                .orElse(null);

        if (existingContract != null) {
            return update(existingContract.getId(), request);
        } else {
            return create(request);
        }
    }

    /**
     * 提交合同审批（配置审批节点并启动审批流程）
     */
    @Transactional
    public Contract submitForApproval(UUID contractId, ApprovalNodeConfigRequest configRequest) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));

        // 删除旧的审批节点
        contractApprovalNodeRepository.deleteByContractId(contractId);

        // 保存新的审批节点配置
        int order = 1;
        if (configRequest.getAreaManagers() != null) {
            for (ApprovalNodeConfigRequest.ApproverRequest approver : configRequest.getAreaManagers()) {
                ContractApprovalNode node = new ContractApprovalNode();
                node.setContract(contract);
                node.setRoleType("片区负责人");
                node.setApproverId(approver.getApproverId());
                node.setApproverName(approver.getApproverName());
                node.setApprovalOrder(order++);
                contractApprovalNodeRepository.save(node);
            }
        }

        if (configRequest.getDepartmentHeads() != null) {
            for (ApprovalNodeConfigRequest.ApproverRequest approver : configRequest.getDepartmentHeads()) {
                ContractApprovalNode node = new ContractApprovalNode();
                node.setContract(contract);
                node.setRoleType("部门负责人");
                node.setApproverId(approver.getApproverId());
                node.setApproverName(approver.getApproverName());
                node.setApprovalOrder(order++);
                contractApprovalNodeRepository.save(node);
            }
        }

        if (configRequest.getCompanyLeaders() != null) {
            for (ApprovalNodeConfigRequest.ApproverRequest approver : configRequest.getCompanyLeaders()) {
                ContractApprovalNode node = new ContractApprovalNode();
                node.setContract(contract);
                node.setRoleType("公司领导");
                node.setApproverId(approver.getApproverId());
                node.setApproverName(approver.getApproverName());
                node.setApprovalOrder(order++);
                contractApprovalNodeRepository.save(node);
            }
        }

        if (configRequest.getFinancialDirectors() != null) {
            for (ApprovalNodeConfigRequest.ApproverRequest approver : configRequest.getFinancialDirectors()) {
                ContractApprovalNode node = new ContractApprovalNode();
                node.setContract(contract);
                node.setRoleType("财务总监");
                node.setApproverId(approver.getApproverId());
                node.setApproverName(approver.getApproverName());
                node.setApprovalOrder(order++);
                contractApprovalNodeRepository.save(node);
            }
        }

        // 更新合同状态
        contract.setContractStatus("审批中");
        contract = contractRepository.save(contract);

        // 创建初始审批状态记录（创建人提交）
        ContractWorkflowStatus initialStatus = new ContractWorkflowStatus();
        initialStatus.setContract(contract);
        initialStatus.setNodeName("创建人");
        initialStatus.setStatus("已提交");
        initialStatus.setOperationTime(LocalDateTime.now());
        contractWorkflowStatusRepository.save(initialStatus);

        // 为每个审批节点创建初始状态记录
        List<ContractApprovalNode> nodes = contractApprovalNodeRepository
                .findByContractIdOrderByApprovalOrderAsc(contractId);
        for (ContractApprovalNode node : nodes) {
            ContractWorkflowStatus status = new ContractWorkflowStatus();
            status.setContract(contract);
            status.setNodeName(node.getRoleType());
            status.setOperatorId(node.getApproverId());
            status.setOperatorName(node.getApproverName());
            status.setStatus("未查看");
            contractWorkflowStatusRepository.save(status);
        }

        return contract;
    }

    /**
     * 查询合同流程审批状态
     */
    @Transactional(readOnly = true)
    public List<ContractWorkflowStatus> getWorkflowStatus(UUID contractId) {
        return contractWorkflowStatusRepository.findByContractIdOrderByCreateTimeAsc(contractId);
    }

    /**
     * 查询合同执行进度
     */
    @Transactional(readOnly = true)
    public ContractExecutionProgress getExecutionProgress(UUID contractId) {
        // 验证合同存在
        contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
        
        // 如果不存在执行进度记录，返回一个默认的空记录
        return contractExecutionProgressRepository.findByContractId(contractId)
                .orElseGet(() -> {
                    ContractExecutionProgress progress = new ContractExecutionProgress();
                    Contract contract = new Contract();
                    contract.setId(contractId);
                    progress.setContract(contract);
                    return progress;
                });
    }

    /**
     * 更新合同执行进度
     */
    @Transactional
    public ContractExecutionProgress updateExecutionProgress(UUID contractId, ContractExecutionProgressRequest request) {
        // 验证合同存在
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("合同不存在"));

        // 查找或创建执行进度记录
        ContractExecutionProgress progress = contractExecutionProgressRepository.findByContractId(contractId)
                .orElseGet(() -> {
                    ContractExecutionProgress newProgress = new ContractExecutionProgress();
                    newProgress.setContract(contract);
                    return newProgress;
                });

        // 更新进度信息
        progress.setDesignProgress(request.getDesignProgress());
        progress.setProductionProgress(request.getProductionProgress());
        progress.setProcurementProgress(request.getProcurementProgress());
        progress.setManufacturingProgress(request.getManufacturingProgress());
        progress.setAssemblyProgress(request.getAssemblyProgress());
        progress.setRemark(request.getRemark());

        return contractExecutionProgressRepository.save(progress);
    }

    /**
     * 查询所有合同的执行进度列表（用于合同执行动态列表）
     * 返回包含合同基本信息和执行进度的响应列表
     * 如果合同没有执行进度记录，也会显示合同基本信息，进度字段为空
     */
    @Transactional(readOnly = true)
    public List<ContractExecutionProgressResponse> getAllExecutionProgress() {
        // 获取所有合同
        List<Contract> allContracts = contractRepository.findAll();
        
        return allContracts.stream()
                .map(contract -> {
                    // 查找该合同的执行进度
                    ContractExecutionProgress progress = contractExecutionProgressRepository
                            .findByContractId(contract.getId())
                            .orElse(null);
                    
                    // 如果有进度记录，使用进度记录创建响应
                    if (progress != null) {
                        return ContractExecutionProgressResponse.fromEntity(progress);
                    } else {
                        // 如果没有进度记录，创建只包含合同基本信息的响应
                        ContractExecutionProgressResponse response = new ContractExecutionProgressResponse();
                        response.setContractId(contract.getId());
                        response.setContractNumber(contract.getContractNumber());
                        response.setContractName(contract.getContractName());
                        // 进度字段保持为null
                        return response;
                    }
                })
                .toList();
    }

    /**
     * 将请求DTO映射到合同实体
     */
    private void mapRequestToContract(ContractRequest request, Contract contract) {
        contract.setContractNumber(request.getContractNumber());
        contract.setContractName(request.getContractName());
        contract.setCustomerName(request.getCustomerName());
        contract.setSalesOpportunity(request.getSalesOpportunity());
        contract.setSigningDate(request.getSigningDate());
        contract.setScheduleDate(request.getScheduleDate());
        contract.setDeliveryDate(request.getDeliveryDate());
        contract.setDeliveryStation(request.getDeliveryStation());
        contract.setFreightPayment(request.getFreightPayment());
        contract.setProjectName(request.getProjectName());
        contract.setPaymentMethod(request.getPaymentMethod());
        contract.setTotalPrice(request.getTotalPrice());
        contract.setContractRemark(request.getContractRemark());
        contract.setOrderingUnit(request.getOrderingUnit());
        contract.setOrderingRepresentative(request.getOrderingRepresentative());
        contract.setOrderingPhone(request.getOrderingPhone());
        contract.setOrderingAddress(request.getOrderingAddress());
        contract.setOrderingPostcode(request.getOrderingPostcode());
        contract.setOrderingArea(request.getOrderingArea());
        contract.setAttachment(request.getAttachment());
        contract.setManagerId(request.getManagerId());
        contract.setHandlerDepartment(request.getHandlerDepartment());
        contract.setHandlerName(request.getHandlerName());
        contract.setHandleDate(request.getHandleDate());
    }

    /**
     * 保存合同细目
     */
    private void saveContractDetails(Contract contract, List<ContractDetailRequest> details) {
        for (ContractDetailRequest detailRequest : details) {
            ContractDetail detail = new ContractDetail();
            detail.setContract(contract);
            detail.setType(detailRequest.getType());
            detail.setProductModel(detailRequest.getProductModel());
            detail.setProductType(detailRequest.getProductType());
            detail.setSubType(detailRequest.getSubType());
            detail.setProductName(detailRequest.getProductName());
            detail.setRemark(detailRequest.getRemark());
            contractDetailRepository.save(detail);
        }
    }

    /**
     * 保存付款阶段
     */
    private void savePaymentStages(Contract contract, List<ContractPaymentStageRequest> stages) {
        for (ContractPaymentStageRequest stageRequest : stages) {
            ContractPaymentStage stage = new ContractPaymentStage();
            stage.setContract(contract);
            stage.setPaymentStage(stageRequest.getPaymentStage());
            stage.setAmountPayable(stageRequest.getAmountPayable());
            stage.setAmountPaid(stageRequest.getAmountPaid());
            stage.setPaymentStageName(stageRequest.getPaymentStageName());
            stage.setPaymentDate(stageRequest.getPaymentDate());
            stage.setRemark(stageRequest.getRemark());
            contractPaymentStageRepository.save(stage);
        }
    }

    /**
     * 保存占比划分
     */
    private void saveProportions(Contract contract, List<ContractProportionRequest> proportions) {
        for (ContractProportionRequest proportionRequest : proportions) {
            ContractProportion proportion = new ContractProportion();
            proportion.setContract(contract);
            proportion.setPersonInChargeId(proportionRequest.getPersonInChargeId());
            proportion.setPersonInChargeName(proportionRequest.getPersonInChargeName());
            proportion.setAffiliatedArea(proportionRequest.getAffiliatedArea());
            proportion.setProportion(proportionRequest.getProportion());
            proportion.setRemark(proportionRequest.getRemark());
            contractProportionRepository.save(proportion);
        }
    }

    /**
     * 初始化合同的关联对象
     */
    private void initializeContractAssociations(Contract contract) {
        // 如果需要懒加载关联对象，可以在这里初始化
        // 目前使用@ManyToOne的默认fetch策略
    }
}

