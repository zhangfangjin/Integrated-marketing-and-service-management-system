package org.example.rootmanage.remotemonitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.remotemonitoring.dto.AlarmAcknowledgeRequest;
import org.example.rootmanage.remotemonitoring.dto.AlarmConfigRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 报警控制器测试类
 */
@WebMvcTest(
        controllers = AlarmController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("报警控制器测试")
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlarmService alarmService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID configId;
    private UUID recordId;
    private UUID pointId;
    private AlarmConfig alarmConfig;
    private AlarmRecord alarmRecord;

    @BeforeEach
    void setUp() {
        configId = UUID.randomUUID();
        recordId = UUID.randomUUID();
        pointId = UUID.randomUUID();

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
        alarmRecord.setStatus(AlarmStatus.ACTIVE);
    }

    @Test
    @DisplayName("获取所有报警配置 - 成功")
    void testFindAllConfigs_Success() throws Exception {
        // Given
        List<AlarmConfig> configs = Arrays.asList(alarmConfig);
        when(alarmService.findAllConfigs()).thenReturn(configs);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/alarms/configs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alarmCode").value("ALARM001"))
                .andExpect(jsonPath("$[0].alarmName").value("超压报警"));

        verify(alarmService, times(1)).findAllConfigs();
    }

    @Test
    @DisplayName("创建报警配置 - 成功")
    void testCreateConfig_Success() throws Exception {
        // Given
        AlarmConfigRequest request = new AlarmConfigRequest();
        request.setAlarmCode("ALARM002");
        request.setAlarmName("超温报警");
        request.setDataPointId(pointId);
        request.setAlarmType(AlarmType.HIGH);
        request.setAlarmLevel(AlarmLevel.ERROR);
        request.setUpperLimit(80.0);
        request.setEnabled(true);

        AlarmConfig createdConfig = new AlarmConfig();
        createdConfig.setId(UUID.randomUUID());
        createdConfig.setAlarmCode("ALARM002");
        createdConfig.setAlarmName("超温报警");

        when(alarmService.createConfig(any(AlarmConfigRequest.class))).thenReturn(createdConfig);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/alarms/configs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alarmCode").value("ALARM002"))
                .andExpect(jsonPath("$.alarmName").value("超温报警"));

        verify(alarmService, times(1)).createConfig(any(AlarmConfigRequest.class));
    }

    @Test
    @DisplayName("获取活动的报警记录 - 成功")
    void testFindActiveAlarms_Success() throws Exception {
        // Given
        List<AlarmRecord> records = Arrays.asList(alarmRecord);
        when(alarmService.findActiveAlarms()).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/alarms/records/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].alarmValue").value(150.0));

        verify(alarmService, times(1)).findActiveAlarms();
    }

    @Test
    @DisplayName("确认报警 - 成功")
    void testAcknowledgeAlarm_Success() throws Exception {
        // Given
        AlarmAcknowledgeRequest request = new AlarmAcknowledgeRequest();
        UUID userId = UUID.randomUUID();
        request.setAcknowledgedById(userId);
        request.setAcknowledgedByName("测试用户");
        request.setHandleRemark("已处理");

        alarmRecord.setStatus(AlarmStatus.ACKNOWLEDGED);
        alarmRecord.setAcknowledgedById(userId);
        alarmRecord.setAcknowledgedByName("测试用户");

        when(alarmService.acknowledgeAlarm(eq(recordId), any(AlarmAcknowledgeRequest.class)))
                .thenReturn(alarmRecord);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/alarms/records/{id}/acknowledge", recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"));

        verify(alarmService, times(1))
                .acknowledgeAlarm(eq(recordId), any(AlarmAcknowledgeRequest.class));
    }

    @Test
    @DisplayName("恢复报警 - 成功")
    void testRecoverAlarm_Success() throws Exception {
        // Given
        alarmRecord.setStatus(AlarmStatus.RECOVERED);
        when(alarmService.recoverAlarm(recordId)).thenReturn(alarmRecord);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/alarms/records/{id}/recover", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(alarmService, times(1)).recoverAlarm(recordId);
    }

    @Test
    @DisplayName("根据状态获取报警记录 - 成功")
    void testFindRecordsByStatus_Success() throws Exception {
        // Given
        List<AlarmRecord> records = Arrays.asList(alarmRecord);
        when(alarmService.findRecordsByStatus(AlarmStatus.ACTIVE)).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/alarms/records/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(alarmService, times(1)).findRecordsByStatus(AlarmStatus.ACTIVE);
    }
}


