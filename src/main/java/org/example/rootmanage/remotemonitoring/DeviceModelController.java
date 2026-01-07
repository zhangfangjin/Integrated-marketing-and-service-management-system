package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.DeviceModelRequest;
import org.example.rootmanage.remotemonitoring.entity.DeviceModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 设备模型控制器
 * 提供设备模型配置的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/device-models")
@RequiredArgsConstructor
public class DeviceModelController {

    private final DeviceModelService deviceModelService;

    /**
     * 获取所有设备模型（支持关键词搜索）
     */
    @GetMapping
    public List<DeviceModel> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return deviceModelService.searchDeviceModels(keyword);
        }
        return deviceModelService.findAll();
    }

    /**
     * 根据设备类型查找
     */
    @GetMapping("/type/{deviceType}")
    public List<DeviceModel> findByDeviceType(@PathVariable String deviceType) {
        return deviceModelService.findByDeviceType(deviceType);
    }

    /**
     * 获取启用的设备模型
     */
    @GetMapping("/enabled")
    public List<DeviceModel> findEnabled() {
        return deviceModelService.findEnabled();
    }

    /**
     * 根据ID获取设备模型
     */
    @GetMapping("/{id}")
    public DeviceModel findById(@PathVariable UUID id) {
        return deviceModelService.findById(id);
    }

    /**
     * 创建设备模型
     */
    @PostMapping
    public DeviceModel create(@Valid @RequestBody DeviceModelRequest request) {
        return deviceModelService.create(request);
    }

    /**
     * 更新设备模型
     */
    @PutMapping("/{id}")
    public DeviceModel update(@PathVariable UUID id, @Valid @RequestBody DeviceModelRequest request) {
        return deviceModelService.update(id, request);
    }

    /**
     * 删除设备模型
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deviceModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

















