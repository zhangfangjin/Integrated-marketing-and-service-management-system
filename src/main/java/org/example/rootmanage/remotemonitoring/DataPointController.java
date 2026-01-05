package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.DataPointRequest;
import org.example.rootmanage.remotemonitoring.dto.ManualDataInputRequest;
import org.example.rootmanage.remotemonitoring.entity.CollectionMode;
import org.example.rootmanage.remotemonitoring.entity.DataPoint;
import org.example.rootmanage.remotemonitoring.entity.DeviceRunningData;
import org.example.rootmanage.remotemonitoring.entity.PointType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 数据点位控制器
 * 提供点位配置和手动数据录入的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/data-points")
@RequiredArgsConstructor
public class DataPointController {

    private final DataPointService dataPointService;

    /**
     * 获取所有点位（支持关键词搜索）
     */
    @GetMapping
    public List<DataPoint> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return dataPointService.searchDataPoints(keyword);
        }
        return dataPointService.findAll();
    }

    /**
     * 根据点位类型查找
     */
    @GetMapping("/type/{pointType}")
    public List<DataPoint> findByPointType(@PathVariable PointType pointType) {
        return dataPointService.findByPointType(pointType);
    }

    /**
     * 根据表计ID查找
     */
    @GetMapping("/meter/{meterId}")
    public List<DataPoint> findByMeterId(@PathVariable UUID meterId) {
        return dataPointService.findByMeterId(meterId);
    }

    /**
     * 根据采集模式查找
     */
    @GetMapping("/collection-mode/{collectionMode}")
    public List<DataPoint> findByCollectionMode(@PathVariable CollectionMode collectionMode) {
        return dataPointService.findByCollectionMode(collectionMode);
    }

    /**
     * 根据ID获取点位
     */
    @GetMapping("/{id}")
    public DataPoint findById(@PathVariable UUID id) {
        return dataPointService.findById(id);
    }

    /**
     * 创建点位
     */
    @PostMapping
    public DataPoint create(@Valid @RequestBody DataPointRequest request) {
        return dataPointService.create(request);
    }

    /**
     * 更新点位
     */
    @PutMapping("/{id}")
    public DataPoint update(@PathVariable UUID id, @Valid @RequestBody DataPointRequest request) {
        return dataPointService.update(id, request);
    }

    /**
     * 删除点位
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dataPointService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 手动录入数据
     */
    @PostMapping("/manual-input")
    public DeviceRunningData inputManualData(@Valid @RequestBody ManualDataInputRequest request) {
        return dataPointService.inputManualData(request);
    }
}





