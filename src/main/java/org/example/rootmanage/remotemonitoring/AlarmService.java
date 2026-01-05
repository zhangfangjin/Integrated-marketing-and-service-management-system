package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.AlarmAcknowledgeRequest;
import org.example.rootmanage.remotemonitoring.dto.AlarmConfigRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.AlarmConfigRepository;
import org.example.rootmanage.remotemonitoring.repository.AlarmRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 报警服务类
 * 提供报警配置和报警记录的管理功能
 */
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmConfigRepository alarmConfigRepository;
    private final AlarmRecordRepository alarmRecordRepository;

    // ==================== 报警配置管理 ====================

    /**
     * 获取所有报警配置
     */
    @Transactional(readOnly = true)
    public List<AlarmConfig> findAllConfigs() {
        return alarmConfigRepository.findAll();
    }

    /**
     * 根据点位ID获取报警配置
     */
    @Transactional(readOnly = true)
    public List<AlarmConfig> findConfigsByDataPointId(UUID dataPointId) {
        return alarmConfigRepository.findByDataPointId(dataPointId);
    }

    /**
     * 搜索报警配置
     */
    @Transactional(readOnly = true)
    public List<AlarmConfig> searchConfigs(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return alarmConfigRepository.searchAlarmConfigs(keyword.trim());
        }
        return alarmConfigRepository.findAll();
    }

    /**
     * 根据ID获取报警配置
     */
    @Transactional(readOnly = true)
    public AlarmConfig findConfigById(UUID id) {
        return alarmConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报警配置不存在"));
    }

    /**
     * 创建报警配置
     */
    @Transactional
    public AlarmConfig createConfig(AlarmConfigRequest request) {
        // 检查编码是否已存在
        alarmConfigRepository.findByAlarmCode(request.getAlarmCode())
                .ifPresent(a -> {
                    throw new IllegalStateException("报警配置编码已存在");
                });

        AlarmConfig config = new AlarmConfig();
        setAlarmConfigProperties(config, request);

        return alarmConfigRepository.save(config);
    }

    /**
     * 更新报警配置
     */
    @Transactional
    public AlarmConfig updateConfig(UUID id, AlarmConfigRequest request) {
        AlarmConfig config = alarmConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报警配置不存在"));

        // 检查编码是否被其他配置使用
        if (!config.getAlarmCode().equals(request.getAlarmCode())) {
            alarmConfigRepository.findByAlarmCode(request.getAlarmCode())
                    .ifPresent(a -> {
                        throw new IllegalStateException("报警配置编码已被其他配置使用");
                    });
        }

        setAlarmConfigProperties(config, request);

        return alarmConfigRepository.save(config);
    }

    /**
     * 删除报警配置
     */
    @Transactional
    public void deleteConfig(UUID id) {
        AlarmConfig config = alarmConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报警配置不存在"));
        alarmConfigRepository.delete(config);
    }

    // ==================== 报警记录管理 ====================

    /**
     * 获取活动的报警记录
     */
    @Transactional(readOnly = true)
    public List<AlarmRecord> findActiveAlarms() {
        return alarmRecordRepository.findActiveAlarms();
    }

    /**
     * 根据状态获取报警记录
     */
    @Transactional(readOnly = true)
    public List<AlarmRecord> findRecordsByStatus(AlarmStatus status) {
        return alarmRecordRepository.findByStatusOrderByAlarmTimeDesc(status);
    }

    /**
     * 根据点位ID获取报警记录
     */
    @Transactional(readOnly = true)
    public List<AlarmRecord> findRecordsByDataPointId(UUID dataPointId) {
        return alarmRecordRepository.findByDataPointIdOrderByAlarmTimeDesc(dataPointId);
    }

    /**
     * 根据时间范围获取报警记录
     */
    @Transactional(readOnly = true)
    public List<AlarmRecord> findRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alarmRecordRepository.findByTimeRange(startTime, endTime);
    }

    /**
     * 根据ID获取报警记录
     */
    @Transactional(readOnly = true)
    public AlarmRecord findRecordById(UUID id) {
        return alarmRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报警记录不存在"));
    }

    /**
     * 触发报警（创建报警记录）
     */
    @Transactional
    public AlarmRecord triggerAlarm(UUID alarmConfigId, Double alarmValue, String alarmMessage) {
        AlarmConfig config = alarmConfigRepository.findById(alarmConfigId)
                .orElseThrow(() -> new IllegalArgumentException("报警配置不存在"));

        AlarmRecord record = new AlarmRecord();
        record.setAlarmConfigId(alarmConfigId);
        record.setDataPointId(config.getDataPointId());
        record.setAlarmType(config.getAlarmType());
        record.setAlarmLevel(config.getAlarmLevel());
        record.setAlarmTime(LocalDateTime.now());
        record.setAlarmValue(alarmValue);
        record.setThresholdValue(config.getUpperLimit() != null ? config.getUpperLimit() : config.getLowerLimit());
        record.setAlarmMessage(alarmMessage != null ? alarmMessage : config.getAlarmMessageTemplate());
        record.setStatus(AlarmStatus.ACTIVE);

        return alarmRecordRepository.save(record);
    }

    /**
     * 确认报警
     */
    @Transactional
    public AlarmRecord acknowledgeAlarm(UUID recordId, AlarmAcknowledgeRequest request) {
        AlarmRecord record = alarmRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("报警记录不存在"));

        if (record.getStatus() != AlarmStatus.ACTIVE) {
            throw new IllegalStateException("只能确认活动状态的报警");
        }

        record.setStatus(AlarmStatus.ACKNOWLEDGED);
        record.setAcknowledgedById(request.getAcknowledgedById());
        record.setAcknowledgedByName(request.getAcknowledgedByName());
        record.setAcknowledgedTime(LocalDateTime.now());
        record.setHandleRemark(request.getHandleRemark());

        return alarmRecordRepository.save(record);
    }

    /**
     * 恢复报警
     */
    @Transactional
    public AlarmRecord recoverAlarm(UUID recordId) {
        AlarmRecord record = alarmRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("报警记录不存在"));

        record.setStatus(AlarmStatus.RECOVERED);
        record.setRecoveryTime(LocalDateTime.now());

        return alarmRecordRepository.save(record);
    }

    /**
     * 检查点位是否超限并触发报警
     */
    @Transactional
    public void checkAndTriggerAlarm(UUID dataPointId, Double value) {
        List<AlarmConfig> configs = alarmConfigRepository.findByDataPointId(dataPointId);

        for (AlarmConfig config : configs) {
            if (!config.getEnabled()) {
                continue;
            }

            boolean shouldAlarm = false;
            String message = null;

            switch (config.getAlarmType()) {
                case HIGH:
                    if (config.getUpperLimit() != null && value > config.getUpperLimit()) {
                        shouldAlarm = true;
                        message = String.format("数值 %.2f 超过上限 %.2f", value, config.getUpperLimit());
                    }
                    break;
                case LOW:
                    if (config.getLowerLimit() != null && value < config.getLowerLimit()) {
                        shouldAlarm = true;
                        message = String.format("数值 %.2f 低于下限 %.2f", value, config.getLowerLimit());
                    }
                    break;
                case RANGE:
                    if ((config.getUpperLimit() != null && value > config.getUpperLimit()) ||
                            (config.getLowerLimit() != null && value < config.getLowerLimit())) {
                        shouldAlarm = true;
                        message = String.format("数值 %.2f 超出范围 [%.2f, %.2f]",
                                value,
                                config.getLowerLimit() != null ? config.getLowerLimit() : Double.MIN_VALUE,
                                config.getUpperLimit() != null ? config.getUpperLimit() : Double.MAX_VALUE);
                    }
                    break;
                default:
                    break;
            }

            if (shouldAlarm) {
                // 检查是否已有活动报警
                List<AlarmRecord> activeRecords = alarmRecordRepository
                        .findByDataPointIdAndStatusOrderByAlarmTimeDesc(dataPointId, AlarmStatus.ACTIVE);
                if (activeRecords.isEmpty()) {
                    triggerAlarm(config.getId(), value, message);
                }
            }
        }
    }

    /**
     * 设置报警配置属性
     */
    private void setAlarmConfigProperties(AlarmConfig config, AlarmConfigRequest request) {
        config.setAlarmCode(request.getAlarmCode());
        config.setAlarmName(request.getAlarmName());
        config.setDataPointId(request.getDataPointId());
        config.setAlarmType(request.getAlarmType() != null ? request.getAlarmType() : AlarmType.HIGH);
        config.setAlarmLevel(request.getAlarmLevel() != null ? request.getAlarmLevel() : AlarmLevel.WARNING);
        config.setUpperLimit(request.getUpperLimit());
        config.setLowerLimit(request.getLowerLimit());
        config.setDeadband(request.getDeadband() != null ? request.getDeadband() : 0.0);
        config.setDelaySeconds(request.getDelaySeconds() != null ? request.getDelaySeconds() : 0);
        config.setAlarmMessageTemplate(request.getAlarmMessageTemplate());
        config.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        config.setNotifyEnabled(request.getNotifyEnabled() != null ? request.getNotifyEnabled() : true);
        config.setNotifyMethod(request.getNotifyMethod());
        config.setNotifyReceivers(request.getNotifyReceivers());
        config.setRemark(request.getRemark());
    }
}





