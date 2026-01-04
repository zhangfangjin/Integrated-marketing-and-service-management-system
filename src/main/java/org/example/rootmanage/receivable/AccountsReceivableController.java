package org.example.rootmanage.receivable;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.receivable.dto.AccountsReceivableResponse;
import org.example.rootmanage.receivable.dto.AccountsReceivableUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 应收账管理Controller
 * 提供应收账计划、应收账录入、应收账查询功能
 */
@RestController
@RequestMapping("/api/receivable")
@RequiredArgsConstructor
public class AccountsReceivableController {

    private final AccountsReceivableService accountsReceivableService;

    /**
     * 获取所有应收账计划
     * 4.5.1 应收账计划
     */
    @GetMapping("/plan")
    public List<AccountsReceivableResponse> getPlan(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return accountsReceivableService.search(keyword);
        }
        return accountsReceivableService.findAll();
    }

    /**
     * 根据合同编号获取应收账计划
     */
    @GetMapping("/plan/contract/{contractNumber}")
    public List<AccountsReceivableResponse> getPlanByContract(@PathVariable String contractNumber) {
        return accountsReceivableService.findByContractNumber(contractNumber);
    }

    /**
     * 应收账查询
     * 4.5.3 应收账查询
     */
    @GetMapping
    public List<AccountsReceivableResponse> search(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return accountsReceivableService.search(keyword);
        }
        return accountsReceivableService.findAll();
    }

    /**
     * 查询未付清的应收账
     */
    @GetMapping("/unpaid")
    public List<AccountsReceivableResponse> getUnpaid() {
        return accountsReceivableService.findUnpaid();
    }

    /**
     * 根据ID获取应收账详情
     */
    @GetMapping("/{id}")
    public AccountsReceivableResponse getById(@PathVariable UUID id) {
        return accountsReceivableService.findById(id);
    }

    /**
     * 应收账录入/修改
     * 4.5.2 应收账录入
     */
    @PutMapping("/{id}")
    public AccountsReceivableResponse update(
            @PathVariable UUID id,
            @RequestBody @Validated AccountsReceivableUpdateRequest request) {
        return accountsReceivableService.update(id, request);
    }
}

