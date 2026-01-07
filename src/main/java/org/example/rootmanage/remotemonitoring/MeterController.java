package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.MeterRequest;
import org.example.rootmanage.remotemonitoring.entity.Meter;
import org.example.rootmanage.remotemonitoring.entity.MeterType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 表计控制器
 * 提供表计配置的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    /**
     * 获取所有表计（支持关键词搜索）
     */
    @GetMapping
    public List<Meter> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return meterService.searchMeters(keyword);
        }
        return meterService.findAll();
    }

    /**
     * 根据表计类型查找
     */
    @GetMapping("/type/{meterType}")
    public List<Meter> findByMeterType(@PathVariable MeterType meterType) {
        return meterService.findByMeterType(meterType);
    }

    /**
     * 根据空间节点ID查找
     */
    @GetMapping("/space-node/{spaceNodeId}")
    public List<Meter> findBySpaceNodeId(@PathVariable UUID spaceNodeId) {
        return meterService.findBySpaceNodeId(spaceNodeId);
    }

    /**
     * 根据设备ID查找
     */
    @GetMapping("/device/{deviceId}")
    public List<Meter> findByDeviceId(@PathVariable UUID deviceId) {
        return meterService.findByDeviceId(deviceId);
    }

    /**
     * 根据ID获取表计
     */
    @GetMapping("/{id}")
    public Meter findById(@PathVariable UUID id) {
        return meterService.findById(id);
    }

    /**
     * 创建表计
     */
    @PostMapping
    public Meter create(@Valid @RequestBody MeterRequest request) {
        return meterService.create(request);
    }

    /**
     * 更新表计
     */
    @PutMapping("/{id}")
    public Meter update(@PathVariable UUID id, @Valid @RequestBody MeterRequest request) {
        return meterService.update(id, request);
    }

    /**
     * 删除表计
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        meterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

















