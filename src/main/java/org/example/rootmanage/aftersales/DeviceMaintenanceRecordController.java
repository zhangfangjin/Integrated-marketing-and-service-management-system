package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.DeviceMaintenanceRecordRequest;
import org.example.rootmanage.aftersales.entity.DeviceMaintenanceRecord;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 设备维护记录管理Controller
 */
@RestController
@RequestMapping("/api/device-maintenance-records")
@RequiredArgsConstructor
public class DeviceMaintenanceRecordController {

    private final DeviceMaintenanceRecordService recordService;

    /**
     * 获取维护记录列表
     */
    @GetMapping
    public List<DeviceMaintenanceRecord> list() {
        return recordService.findAll();
    }

    /**
     * 根据设备ID获取维护记录列表
     */
    @GetMapping("/device/{deviceId}")
    public List<DeviceMaintenanceRecord> listByDevice(@PathVariable UUID deviceId) {
        return recordService.findByDeviceId(deviceId);
    }

    /**
     * 根据设备编号获取维护记录列表
     */
    @GetMapping("/device-number/{deviceNumber}")
    public List<DeviceMaintenanceRecord> listByDeviceNumber(@PathVariable String deviceNumber) {
        return recordService.findByDeviceNumber(deviceNumber);
    }

    /**
     * 根据ID获取维护记录详情
     */
    @GetMapping("/{id}")
    public DeviceMaintenanceRecord getById(@PathVariable UUID id) {
        return recordService.findById(id);
    }

    /**
     * 创建维护记录
     */
    @PostMapping
    public DeviceMaintenanceRecord create(@RequestBody @Validated DeviceMaintenanceRecordRequest request) {
        return recordService.create(request);
    }

    /**
     * 更新维护记录
     */
    @PutMapping("/{id}")
    public DeviceMaintenanceRecord update(@PathVariable UUID id, @RequestBody @Validated DeviceMaintenanceRecordRequest request) {
        return recordService.update(id, request);
    }

    /**
     * 删除维护记录
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        recordService.delete(id);
    }
}

















