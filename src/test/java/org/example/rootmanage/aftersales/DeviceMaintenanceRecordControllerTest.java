package org.example.rootmanage.aftersales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.aftersales.dto.DeviceMaintenanceRecordRequest;
import org.example.rootmanage.aftersales.entity.DeviceMaintenanceRecord;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备维护记录控制器测试类
 */
@WebMvcTest(
        controllers = DeviceMaintenanceRecordController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("设备维护记录控制器测试")
class DeviceMaintenanceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceMaintenanceRecordService maintenanceRecordService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID recordId;
    private UUID deviceId;
    private DeviceMaintenanceRecord record;

    @BeforeEach
    void setUp() {
        recordId = UUID.randomUUID();
        deviceId = UUID.randomUUID();

        record = new DeviceMaintenanceRecord();
        record.setId(recordId);
        record.setMaintenanceDate(Date.valueOf(LocalDate.now()));
        record.setFaultReason("故障原因");
        record.setSolution("解决方案");
    }

    @Test
    @DisplayName("获取维护记录列表 - 成功")
    void testList_Success() throws Exception {
        // Given
        List<DeviceMaintenanceRecord> records = Arrays.asList(record);
        when(maintenanceRecordService.findAll()).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/device-maintenance-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].faultReason").value("故障原因"));

        verify(maintenanceRecordService, times(1)).findAll();
    }

    @Test
    @DisplayName("根据设备ID获取维护记录 - 成功")
    void testFindByDeviceId_Success() throws Exception {
        // Given
        List<DeviceMaintenanceRecord> records = Arrays.asList(record);
        when(maintenanceRecordService.findByDeviceId(deviceId)).thenReturn(records);

        // When & Then
        mockMvc.perform(get("/api/device-maintenance-records/device/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(recordId.toString()));

        verify(maintenanceRecordService, times(1)).findByDeviceId(deviceId);
    }

    @Test
    @DisplayName("创建维护记录 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        DeviceMaintenanceRecordRequest request = new DeviceMaintenanceRecordRequest();
        request.setDeviceId(deviceId);
        request.setMaintenanceDate(Date.valueOf(LocalDate.now()));
        request.setFaultReason("新故障");
        request.setSolution("新方案");

        DeviceMaintenanceRecord createdRecord = new DeviceMaintenanceRecord();
        createdRecord.setId(UUID.randomUUID());
        createdRecord.setFaultReason("新故障");

        when(maintenanceRecordService.create(any(DeviceMaintenanceRecordRequest.class)))
                .thenReturn(createdRecord);

        // When & Then
        mockMvc.perform(post("/api/device-maintenance-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faultReason").value("新故障"));

        verify(maintenanceRecordService, times(1))
                .create(any(DeviceMaintenanceRecordRequest.class));
    }

    @Test
    @DisplayName("更新维护记录 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        DeviceMaintenanceRecordRequest request = new DeviceMaintenanceRecordRequest();
        request.setDeviceId(deviceId);
        request.setFaultReason("更新后的故障");
        request.setSolution("更新后的方案");

        record.setFaultReason("更新后的故障");
        record.setSolution("更新后的方案");

        when(maintenanceRecordService.update(eq(recordId), any(DeviceMaintenanceRecordRequest.class)))
                .thenReturn(record);

        // When & Then
        mockMvc.perform(put("/api/device-maintenance-records/{id}", recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faultReason").value("更新后的故障"));

        verify(maintenanceRecordService, times(1))
                .update(eq(recordId), any(DeviceMaintenanceRecordRequest.class));
    }

    @Test
    @DisplayName("删除维护记录 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(maintenanceRecordService).delete(recordId);

        // When & Then
        mockMvc.perform(delete("/api/device-maintenance-records/{id}", recordId))
                .andExpect(status().isOk());

        verify(maintenanceRecordService, times(1)).delete(recordId);
    }
}


