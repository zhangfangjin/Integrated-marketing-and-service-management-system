package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.MaintenanceProgressRequest;
import org.example.rootmanage.aftersales.entity.MaintenanceProgress;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 维修进度管理Controller
 */
@RestController
@RequestMapping("/api/maintenance-progress")
@RequiredArgsConstructor
public class MaintenanceProgressController {

    private final MaintenanceProgressService maintenanceProgressService;

    /**
     * 获取维修进度列表
     */
    @GetMapping
    public List<MaintenanceProgress> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return maintenanceProgressService.searchMaintenanceProgress(keyword.trim());
        }
        return maintenanceProgressService.findAll();
    }

    /**
     * 根据合同ID获取维修进度
     */
    @GetMapping("/contract/{contractId}")
    public MaintenanceProgress getByContractId(@PathVariable UUID contractId) {
        MaintenanceProgress progress = maintenanceProgressService.findByContractId(contractId);
        if (progress == null) {
            // 如果不存在，返回一个空对象或抛出异常，根据业务需求决定
            throw new IllegalArgumentException("该合同暂无维修进度信息");
        }
        return progress;
    }

    /**
     * 根据ID获取维修进度详情
     */
    @GetMapping("/{id}")
    public MaintenanceProgress getById(@PathVariable UUID id) {
        return maintenanceProgressService.findById(id);
    }

    /**
     * 创建或更新维修进度
     */
    @PostMapping
    public MaintenanceProgress save(@RequestBody @Validated MaintenanceProgressRequest request) {
        return maintenanceProgressService.save(request);
    }

    /**
     * 更新维修进度
     */
    @PutMapping("/{id}")
    public MaintenanceProgress update(@PathVariable UUID id, @RequestBody @Validated MaintenanceProgressRequest request) {
        // 这里可以添加ID验证逻辑，或者直接使用save方法
        return maintenanceProgressService.save(request);
    }

    /**
     * 删除维修进度
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        maintenanceProgressService.delete(id);
    }
}

















