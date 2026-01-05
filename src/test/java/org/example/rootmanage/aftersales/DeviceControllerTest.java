package org.example.rootmanage.aftersales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.aftersales.dto.DeviceRequest;
import org.example.rootmanage.aftersales.entity.Device;
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
 * 设备控制器测试类
 */
@WebMvcTest(
        controllers = DeviceController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("设备控制器测试")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID deviceId;
    private Device device;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();

        device = new Device();
        device.setId(deviceId);
        device.setDeviceNumber("DEV001");
        device.setDeviceName("测试设备");
        device.setDeviceModel("MODEL001");
    }

    @Test
    @DisplayName("获取设备列表 - 成功")
    void testList_Success() throws Exception {
        // Given
        List<Device> devices = Arrays.asList(device);
        when(deviceService.findAll()).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceNumber").value("DEV001"))
                .andExpect(jsonPath("$[0].deviceName").value("测试设备"));

        verify(deviceService, times(1)).findAll();
    }

    @Test
    @DisplayName("获取设备列表 - 带关键词搜索")
    void testList_WithKeyword() throws Exception {
        // Given
        List<Device> devices = Arrays.asList(device);
        when(deviceService.searchDevices("测试")).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceName").value("测试设备"));

        verify(deviceService, times(1)).searchDevices("测试");
    }

    @Test
    @DisplayName("根据ID获取设备 - 成功")
    void testGetById_Success() throws Exception {
        // Given
        when(deviceService.findById(deviceId)).thenReturn(device);

        // When & Then
        mockMvc.perform(get("/api/devices/{id}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceNumber").value("DEV001"))
                .andExpect(jsonPath("$.deviceName").value("测试设备"));

        verify(deviceService, times(1)).findById(deviceId);
    }

    @Test
    @DisplayName("创建设备 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        DeviceRequest request = new DeviceRequest();
        request.setDeviceNumber("DEV002");
        request.setDeviceName("新设备");
        request.setDeviceModel("MODEL002");
        request.setProductionDate(Date.valueOf(LocalDate.now()));

        Device createdDevice = new Device();
        createdDevice.setId(UUID.randomUUID());
        createdDevice.setDeviceNumber("DEV002");
        createdDevice.setDeviceName("新设备");

        when(deviceService.create(any(DeviceRequest.class))).thenReturn(createdDevice);

        // When & Then
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceNumber").value("DEV002"))
                .andExpect(jsonPath("$.deviceName").value("新设备"));

        verify(deviceService, times(1)).create(any(DeviceRequest.class));
    }

    @Test
    @DisplayName("更新设备 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        DeviceRequest request = new DeviceRequest();
        request.setDeviceNumber("DEV001");
        request.setDeviceName("更新后的设备");
        request.setDeviceModel("MODEL003");

        device.setDeviceName("更新后的设备");
        device.setDeviceModel("MODEL003");

        when(deviceService.update(eq(deviceId), any(DeviceRequest.class))).thenReturn(device);

        // When & Then
        mockMvc.perform(put("/api/devices/{id}", deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceName").value("更新后的设备"));

        verify(deviceService, times(1)).update(eq(deviceId), any(DeviceRequest.class));
    }

    @Test
    @DisplayName("删除设备 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(deviceService).delete(deviceId);

        // When & Then
        mockMvc.perform(delete("/api/devices/{id}", deviceId))
                .andExpect(status().isOk());

        verify(deviceService, times(1)).delete(deviceId);
    }

    @Test
    @DisplayName("根据合同ID获取设备列表 - 成功")
    void testFindByContractId_Success() throws Exception {
        // Given
        UUID contractId = UUID.randomUUID();
        List<Device> devices = Arrays.asList(device);
        when(deviceService.findByContractId(contractId)).thenReturn(devices);

        // When & Then
        mockMvc.perform(get("/api/devices/contract/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceNumber").value("DEV001"));

        verify(deviceService, times(1)).findByContractId(contractId);
    }
}


