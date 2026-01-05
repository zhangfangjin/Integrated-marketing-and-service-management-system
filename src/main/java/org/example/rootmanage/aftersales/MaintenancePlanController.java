package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.MaintenancePlanRequest;
import org.example.rootmanage.aftersales.entity.MaintenancePlan;
import org.example.rootmanage.aftersales.entity.PlanType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 维护计划管理Controller
 */
@RestController
@RequestMapping("/api/maintenance-plans")
@RequiredArgsConstructor
public class MaintenancePlanController {

    private final MaintenancePlanService planService;

    /**
     * 获取维护计划列表
     */
    @GetMapping
    public List<MaintenancePlan> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) PlanType planType) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return planService.searchMaintenancePlans(keyword.trim());
        }
        if (planType != null) {
            return planService.findByPlanType(planType);
        }
        return planService.findAll();
    }

    /**
     * 根据设备类型获取维护计划（设备类型级别）
     */
    @GetMapping("/device-type/{deviceType}")
    public MaintenancePlan getByDeviceType(@PathVariable String deviceType) {
        MaintenancePlan plan = planService.findByDeviceType(deviceType);
        if (plan == null) {
            throw new IllegalArgumentException("该设备类型暂无维护计划");
        }
        return plan;
    }

    /**
     * 根据设备ID获取维护计划（具体设备级别）
     */
    @GetMapping("/device/{deviceId}")
    public MaintenancePlan getByDeviceId(@PathVariable UUID deviceId) {
        MaintenancePlan plan = planService.findByDeviceId(deviceId);
        if (plan == null) {
            throw new IllegalArgumentException("该设备暂无维护计划");
        }
        return plan;
    }

    /**
     * 根据ID获取维护计划详情
     */
    @GetMapping("/{id}")
    public MaintenancePlan getById(@PathVariable UUID id) {
        return planService.findById(id);
    }

    /**
     * 创建维护计划
     */
    @PostMapping
    public MaintenancePlan create(@RequestBody @Validated MaintenancePlanRequest request) {
        return planService.create(request);
    }

    /**
     * 更新维护计划
     */
    @PutMapping("/{id}")
    public MaintenancePlan update(@PathVariable UUID id, @RequestBody @Validated MaintenancePlanRequest request) {
        return planService.update(id, request);
    }

    /**
     * 删除维护计划
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        planService.delete(id);
    }
}

