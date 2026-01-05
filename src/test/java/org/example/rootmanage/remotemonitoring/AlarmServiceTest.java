package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.AlarmAcknowledgeRequest;
import org.example.rootmanage.remotemonitoring.dto.AlarmConfigRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.AlarmConfigRepository;
import org.example.rootmanage.remotemonitoring.repository.AlarmRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 报警服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("报警服务测试")
class AlarmServiceTest {

    @Mock
    private AlarmConfigRepository alarmConfigRepository;

    @Mock
    private AlarmRecordRepository alarmRecordRepository;

    @InjectMocks
    private AlarmService alarmService;

    private UUID configId;
    private UUID pointId;
    private UUID recordId;
    private AlarmConfig alarmConfig;
    private AlarmRecord alarmRecord;

    @BeforeEach
    void setUp() {
        configId = UUID.randomUUID();
        pointId = UUID.randomUUID();
        recordId = UUID.randomUUID();

        alarmConfig = new AlarmConfig();
        alarmConfig.setId(configId);
        alarmConfig.setAlarmCode("ALARM001");
        alarmConfig.setAlarmName("超压报警");
        alarmConfig.setDataPointId(pointId);
        alarmConfig.setAlarmType(AlarmType.HIGH);
        alarmConfig.setAlarmLevel(AlarmLevel.WARNING);
        alarmConfig.setUpperLimit(100.0);
        alarmConfig.setEnabled(true);

        alarmRecord = new AlarmRecord();
        alarmRecord.setId(recordId);
        alarmRecord.setAlarmConfigId(configId);
        alarmRecord.setDataPointId(pointId);
        alarmRecord.setAlarmType(AlarmType.HIGH);
        alarmRecord.setAlarmLevel(AlarmLevel.WARNING);
        alarmRecord.setAlarmTime(LocalDateTime.now());
        alarmRecord.setAlarmValue(150.0);
        alarmRecord.setThresholdValue(100.0);
        alarmRecord.setStatus(AlarmStatus.ACTIVE);
    }

    @Test
    @DisplayName("创建报警配置 - 成功")
    void testCreateConfig_Success() {
        // Given
        AlarmConfigRequest request = new AlarmConfigRequest();
        request.setAlarmCode("ALARM002");
        request.setAlarmName("超温报警");
        request.setDataPointId(pointId);
        request.setAlarmType(AlarmType.HIGH);
        request.setAlarmLevel(AlarmLevel.ERROR);
        request.setUpperLimit(80.0);
        request.setEnabled(true);

        when(alarmConfigRepository.findByAlarmCode("ALARM002")).thenReturn(Optional.empty());
        when(alarmConfigRepository.save(any(AlarmConfig.class))).thenAnswer(invocation -> {
            AlarmConfig config = invocation.getArgument(0);
            config.setId(UUID.randomUUID());
            return config;
        });

        // When
        AlarmConfig result = alarmService.createConfig(request);

        // Then
        assertNotNull(result);
        assertEquals("ALARM002", result.getAlarmCode());
        assertEquals("超温报警", result.getAlarmName());
        assertEquals(AlarmType.HIGH, result.getAlarmType());
        assertEquals(AlarmLevel.ERROR, result.getAlarmLevel());
        assertEquals(80.0, result.getUpperLimit());
        verify(alarmConfigRepository, times(1)).findByAlarmCode("ALARM002");
        verify(alarmConfigRepository, times(1)).save(any(AlarmConfig.class));
    }

    @Test
    @DisplayName("创建报警配置 - 编码已存在，抛出异常")
    void testCreateConfig_CodeExists() {
        // Given
        AlarmConfigRequest request = new AlarmConfigRequest();
        request.setAlarmCode("ALARM001");

        when(alarmConfigRepository.findByAlarmCode("ALARM001")).thenReturn(Optional.of(alarmConfig));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            alarmService.createConfig(request);
        });
        verify(alarmConfigRepository, times(1)).findByAlarmCode("ALARM001");
        verify(alarmConfigRepository, never()).save(any());
    }

    @Test
    @DisplayName("触发报警 - 成功")
    void testTriggerAlarm_Success() {
        // Given
        Double alarmValue = 150.0;
        String alarmMessage = "数值超过上限";

        when(alarmConfigRepository.findById(configId)).thenReturn(Optional.of(alarmConfig));
        when(alarmRecordRepository.save(any(AlarmRecord.class))).thenAnswer(invocation -> {
            AlarmRecord record = invocation.getArgument(0);
            record.setId(UUID.randomUUID());
            return record;
        });

        // When
        AlarmRecord result = alarmService.triggerAlarm(configId, alarmValue, alarmMessage);

        // Then
        assertNotNull(result);
        assertEquals(configId, result.getAlarmConfigId());
        assertEquals(pointId, result.getDataPointId());
        assertEquals(AlarmType.HIGH, result.getAlarmType());
        assertEquals(AlarmLevel.WARNING, result.getAlarmLevel());
        assertEquals(alarmValue, result.getAlarmValue());
        assertEquals(100.0, result.getThresholdValue());
        assertEquals(alarmMessage, result.getAlarmMessage());
        assertEquals(AlarmStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getAlarmTime());
        verify(alarmConfigRepository, times(1)).findById(configId);
        verify(alarmRecordRepository, times(1)).save(any(AlarmRecord.class));
    }

    @Test
    @DisplayName("确认报警 - 成功")
    void testAcknowledgeAlarm_Success() {
        // Given
        AlarmAcknowledgeRequest request = new AlarmAcknowledgeRequest();
        UUID userId = UUID.randomUUID();
        request.setAcknowledgedById(userId);
        request.setAcknowledgedByName("测试用户");
        request.setHandleRemark("已处理");

        when(alarmRecordRepository.findById(recordId)).thenReturn(Optional.of(alarmRecord));
        when(alarmRecordRepository.save(any(AlarmRecord.class))).thenReturn(alarmRecord);

        // When
        AlarmRecord result = alarmService.acknowledgeAlarm(recordId, request);

        // Then
        assertNotNull(result);
        assertEquals(AlarmStatus.ACKNOWLEDGED, result.getStatus());
        assertEquals(userId, result.getAcknowledgedById());
        assertEquals("测试用户", result.getAcknowledgedByName());
        assertEquals("已处理", result.getHandleRemark());
        assertNotNull(result.getAcknowledgedTime());
        verify(alarmRecordRepository, times(1)).findById(recordId);
        verify(alarmRecordRepository, times(1)).save(any(AlarmRecord.class));
    }

    @Test
    @DisplayName("确认报警 - 非活动状态，抛出异常")
    void testAcknowledgeAlarm_NotActive() {
        // Given
        alarmRecord.setStatus(AlarmStatus.RECOVERED);
        AlarmAcknowledgeRequest request = new AlarmAcknowledgeRequest();
        request.setAcknowledgedById(UUID.randomUUID());

        when(alarmRecordRepository.findById(recordId)).thenReturn(Optional.of(alarmRecord));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            alarmService.acknowledgeAlarm(recordId, request);
        });
        verify(alarmRecordRepository, times(1)).findById(recordId);
        verify(alarmRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("恢复报警 - 成功")
    void testRecoverAlarm_Success() {
        // Given
        when(alarmRecordRepository.findById(recordId)).thenReturn(Optional.of(alarmRecord));
        when(alarmRecordRepository.save(any(AlarmRecord.class))).thenReturn(alarmRecord);

        // When
        AlarmRecord result = alarmService.recoverAlarm(recordId);

        // Then
        assertNotNull(result);
        assertEquals(AlarmStatus.RECOVERED, result.getStatus());
        assertNotNull(result.getRecoveryTime());
        verify(alarmRecordRepository, times(1)).findById(recordId);
        verify(alarmRecordRepository, times(1)).save(any(AlarmRecord.class));
    }

    @Test
    @DisplayName("检查并触发报警 - 超上限")
    void testCheckAndTriggerAlarm_ExceedsUpperLimit() {
        // Given
        Double value = 150.0;
        when(alarmConfigRepository.findByDataPointId(pointId))
                .thenReturn(Arrays.asList(alarmConfig));
        when(alarmRecordRepository.findByDataPointIdAndStatusOrderByAlarmTimeDesc(
                pointId, AlarmStatus.ACTIVE)).thenReturn(List.of());
        when(alarmConfigRepository.findById(configId)).thenReturn(Optional.of(alarmConfig));
        when(alarmRecordRepository.save(any(AlarmRecord.class))).thenAnswer(invocation -> {
            AlarmRecord record = invocation.getArgument(0);
            record.setId(UUID.randomUUID());
            return record;
        });

        // When
        alarmService.checkAndTriggerAlarm(pointId, value);

        // Then
        verify(alarmConfigRepository, times(1)).findByDataPointId(pointId);
        verify(alarmRecordRepository, times(1)).findByDataPointIdAndStatusOrderByAlarmTimeDesc(
                pointId, AlarmStatus.ACTIVE);
        verify(alarmRecordRepository, times(1)).save(any(AlarmRecord.class));
    }

    @Test
    @DisplayName("检查并触发报警 - 未超限，不触发")
    void testCheckAndTriggerAlarm_WithinLimit() {
        // Given
        Double value = 50.0;
        when(alarmConfigRepository.findByDataPointId(pointId))
                .thenReturn(Arrays.asList(alarmConfig));

        // When
        alarmService.checkAndTriggerAlarm(pointId, value);

        // Then
        verify(alarmConfigRepository, times(1)).findByDataPointId(pointId);
        verify(alarmRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("获取活动的报警记录 - 成功")
    void testFindActiveAlarms_Success() {
        // Given
        when(alarmRecordRepository.findActiveAlarms()).thenReturn(Arrays.asList(alarmRecord));

        // When
        List<AlarmRecord> result = alarmService.findActiveAlarms();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recordId, result.get(0).getId());
        verify(alarmRecordRepository, times(1)).findActiveAlarms();
    }
}





