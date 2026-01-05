package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.AfterSalesActivityRequest;
import org.example.rootmanage.aftersales.dto.AfterSalesOrderRequest;
import org.example.rootmanage.aftersales.dto.AfterSalesOrderResponse;
import org.example.rootmanage.aftersales.dto.AssignHandlerRequest;
import org.example.rootmanage.aftersales.dto.CompleteServiceRequest;
import org.example.rootmanage.aftersales.dto.EvaluateServiceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesActivity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 售后服务管理Controller
 * 提供售后服务管理的接口
 */
@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSalesController {

    private final AfterSalesService afterSalesService;

    /**
     * 获取售后服务单列表
     * 支持按关键词搜索（服务单号、合同编号、合同名称、业主单位）
     */
    @GetMapping
    public List<AfterSalesOrderResponse> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return afterSalesService.searchAfterSalesOrders(keyword.trim());
        }
        return afterSalesService.findAll();
    }

    /**
     * 根据ID获取售后服务单详情
     */
    @GetMapping("/{id}")
    public AfterSalesOrderResponse getById(@PathVariable UUID id) {
        return afterSalesService.findById(id);
    }

    /**
     * 新增售后服务单
     */
    @PostMapping
    public AfterSalesOrderResponse create(@RequestBody @Validated AfterSalesOrderRequest request) {
        return afterSalesService.create(request);
    }

    /**
     * 修改售后服务单
     */
    @PutMapping("/{id}")
    public AfterSalesOrderResponse update(@PathVariable UUID id, @RequestBody @Validated AfterSalesOrderRequest request) {
        return afterSalesService.update(id, request);
    }

    /**
     * 删除售后服务单
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        afterSalesService.delete(id);
    }

    /**
     * 指派受理人员（生成服务单并分配）
     */
    @PostMapping("/{id}/assign-handler")
    public AfterSalesOrderResponse assignHandler(@PathVariable UUID id, @RequestBody @Validated AssignHandlerRequest request) {
        return afterSalesService.assignHandler(id, request);
    }

    /**
     * 根据受理人员ID查询待办事宜
     */
    @GetMapping("/pending/{handlerId}")
    public List<AfterSalesOrderResponse> findPendingOrdersByHandler(@PathVariable UUID handlerId) {
        return afterSalesService.findPendingOrdersByHandler(handlerId);
    }

    /**
     * 添加售后服务活动（跟踪记录）
     */
    @PostMapping("/activities")
    public AfterSalesActivity addActivity(@RequestBody @Validated AfterSalesActivityRequest request) {
        return afterSalesService.addActivity(request);
    }

    /**
     * 查询售后服务单的活动列表
     */
    @GetMapping("/{id}/activities")
    public List<AfterSalesActivity> findActivitiesByOrderId(@PathVariable UUID id) {
        return afterSalesService.findActivitiesByOrderId(id);
    }

    /**
     * 完成售后服务
     */
    @PostMapping("/{id}/complete")
    public AfterSalesOrderResponse completeService(@PathVariable UUID id, @RequestBody @Validated CompleteServiceRequest request) {
        return afterSalesService.completeService(id, request);
    }

    /**
     * 评价售后服务
     */
    @PostMapping("/{id}/evaluate")
    public AfterSalesOrderResponse evaluateService(@PathVariable UUID id, @RequestBody @Validated EvaluateServiceRequest request) {
        return afterSalesService.evaluateService(id, request);
    }

    /**
     * 查询已完成的售后服务单（用于评价列表）
     */
    @GetMapping("/completed")
    public List<AfterSalesOrderResponse> findCompletedOrders() {
        return afterSalesService.findCompletedOrders();
    }

    /**
     * 查询需要回访的售后服务单（已完成但未评价）
     */
    @GetMapping("/for-evaluation")
    public List<AfterSalesOrderResponse> findOrdersForEvaluation() {
        return afterSalesService.findOrdersForEvaluation();
    }
}

