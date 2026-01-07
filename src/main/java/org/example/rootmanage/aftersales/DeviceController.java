package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.DeviceRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 设备管理Controller
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 获取设备列表
     */
    @GetMapping
    public List<Device> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return deviceService.searchDevices(keyword.trim());
        }
        return deviceService.findAll();
    }

    /**
     * 根据合同ID获取设备列表
     */
    @GetMapping("/contract/{contractId}")
    public List<Device> listByContract(@PathVariable UUID contractId) {
        return deviceService.findByContractId(contractId);
    }

    /**
     * 根据ID获取设备详情
     */
    @GetMapping("/{id}")
    public Device getById(@PathVariable UUID id) {
        return deviceService.findById(id);
    }

    /**
     * 创建设备
     */
    @PostMapping
    public Device create(@RequestBody @Validated DeviceRequest request) {
        return deviceService.create(request);
    }

    /**
     * 更新设备
     */
    @PutMapping("/{id}")
    public Device update(@PathVariable UUID id, @RequestBody @Validated DeviceRequest request) {
        return deviceService.update(id, request);
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        deviceService.delete(id);
    }
}

















