package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.AlarmAcknowledgeRequest;
import org.example.rootmanage.remotemonitoring.dto.AlarmConfigRequest;
import org.example.rootmanage.remotemonitoring.entity.AlarmConfig;
import org.example.rootmanage.remotemonitoring.entity.AlarmRecord;
import org.example.rootmanage.remotemonitoring.entity.AlarmStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 报警控制器
 * 提供报警配置和报警记录管理的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // ==================== 报警配置管理 ====================

    /**
     * 获取所有报警配置（支持关键词搜索）
     */
    @GetMapping("/configs")
    public List<AlarmConfig> findAllConfigs(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return alarmService.searchConfigs(keyword);
        }
        return alarmService.findAllConfigs();
    }

    /**
     * 根据点位ID获取报警配置
     */
    @GetMapping("/configs/data-point/{dataPointId}")
    public List<AlarmConfig> findConfigsByDataPointId(@PathVariable UUID dataPointId) {
        return alarmService.findConfigsByDataPointId(dataPointId);
    }

    /**
     * 根据ID获取报警配置
     */
    @GetMapping("/configs/{id}")
    public AlarmConfig findConfigById(@PathVariable UUID id) {
        return alarmService.findConfigById(id);
    }

    /**
     * 创建报警配置
     */
    @PostMapping("/configs")
    public AlarmConfig createConfig(@Valid @RequestBody AlarmConfigRequest request) {
        return alarmService.createConfig(request);
    }

    /**
     * 更新报警配置
     */
    @PutMapping("/configs/{id}")
    public AlarmConfig updateConfig(@PathVariable UUID id, @Valid @RequestBody AlarmConfigRequest request) {
        return alarmService.updateConfig(id, request);
    }

    /**
     * 删除报警配置
     */
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable UUID id) {
        alarmService.deleteConfig(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 报警记录管理 ====================

    /**
     * 获取活动的报警记录
     */
    @GetMapping("/records/active")
    public List<AlarmRecord> findActiveAlarms() {
        return alarmService.findActiveAlarms();
    }

    /**
     * 根据状态获取报警记录
     */
    @GetMapping("/records/status/{status}")
    public List<AlarmRecord> findRecordsByStatus(@PathVariable AlarmStatus status) {
        return alarmService.findRecordsByStatus(status);
    }

    /**
     * 根据点位ID获取报警记录
     */
    @GetMapping("/records/data-point/{dataPointId}")
    public List<AlarmRecord> findRecordsByDataPointId(@PathVariable UUID dataPointId) {
        return alarmService.findRecordsByDataPointId(dataPointId);
    }

    /**
     * 根据时间范围获取报警记录
     */
    @GetMapping("/records/time-range")
    public List<AlarmRecord> findRecordsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return alarmService.findRecordsByTimeRange(startTime, endTime);
    }

    /**
     * 根据ID获取报警记录
     */
    @GetMapping("/records/{id}")
    public AlarmRecord findRecordById(@PathVariable UUID id) {
        return alarmService.findRecordById(id);
    }

    /**
     * 确认报警
     */
    @PostMapping("/records/{id}/acknowledge")
    public AlarmRecord acknowledgeAlarm(
            @PathVariable UUID id,
            @Valid @RequestBody AlarmAcknowledgeRequest request) {
        return alarmService.acknowledgeAlarm(id, request);
    }

    /**
     * 恢复报警
     */
    @PostMapping("/records/{id}/recover")
    public AlarmRecord recoverAlarm(@PathVariable UUID id) {
        return alarmService.recoverAlarm(id);
    }
}





