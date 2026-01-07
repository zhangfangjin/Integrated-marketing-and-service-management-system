package org.example.rootmanage.contract;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.contract.dto.ApprovalNodeConfigRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressResponse;
import org.example.rootmanage.contract.dto.ContractRequest;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractExecutionProgress;
import org.example.rootmanage.contract.entity.ContractWorkflowStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 合同管理Controller
 * 提供合同的基本信息管理接口
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * 获取合同列表
     * 支持按关键词搜索（合同编号、合同名称、客户名称）
     */
    @GetMapping
    public List<Contract> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return contractService.searchContracts(keyword.trim());
        }
        return contractService.findAll();
    }

    /**
     * 根据ID获取合同详情
     */
    @GetMapping("/{id}")
    public Contract getById(@PathVariable UUID id) {
        return contractService.findById(id);
    }

    /**
     * 新增合同
     */
    @PostMapping
    public Contract create(@RequestBody @Validated ContractRequest request) {
        return contractService.create(request);
    }

    /**
     * 修改合同
     */
    @PutMapping("/{id}")
    public Contract update(@PathVariable UUID id, @RequestBody @Validated ContractRequest request) {
        return contractService.update(id, request);
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        contractService.delete(id);
    }

    /**
     * 保存合同（不提交审批）
     */
    @PostMapping("/save")
    public Contract save(@RequestBody @Validated ContractRequest request) {
        return contractService.save(request);
    }

    /**
     * 提交合同审批
     * 需要先配置审批节点，然后启动审批流程
     */
    @PostMapping("/{id}/submit")
    public Contract submitForApproval(@PathVariable UUID id, @RequestBody @Validated ApprovalNodeConfigRequest configRequest) {
        return contractService.submitForApproval(id, configRequest);
    }

    /**
     * 审批合同
     */
    @PostMapping("/{id}/approve")
    public void approve(@PathVariable UUID id, @RequestParam boolean approved, @RequestParam(required = false) String remark) {
        contractService.approve(id, approved, remark);
    }

    /**
     * 查看流程审批状态
     */
    @GetMapping("/{id}/workflow-status")
    public List<ContractWorkflowStatus> getWorkflowStatus(@PathVariable UUID id) {
        return contractService.getWorkflowStatus(id);
    }

    /**
     * 查询合同执行进度
     * 打开指定合同后，可查询合同设计、加工、采购、装配等执行进度
     */
    @GetMapping("/{id}/execution-progress")
    public ContractExecutionProgress getExecutionProgress(@PathVariable UUID id) {
        return contractService.getExecutionProgress(id);
    }

    /**
     * 更新合同执行进度
     */
    @PutMapping("/{id}/execution-progress")
    public ContractExecutionProgress updateExecutionProgress(
            @PathVariable UUID id,
            @RequestBody @Validated ContractExecutionProgressRequest request) {
        return contractService.updateExecutionProgress(id, request);
    }

    /**
     * 查询所有合同的执行进度列表（合同执行动态列表）
     * 返回合同编号、合同名称以及各个执行进度
     */
    @GetMapping("/execution-progress/list")
    public List<ContractExecutionProgressResponse> getAllExecutionProgress() {
        return contractService.getAllExecutionProgress();
    }
}

