package org.example.rootmanage.remotemonitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.remotemonitoring.dto.DataPointRequest;
import org.example.rootmanage.remotemonitoring.dto.ManualDataInputRequest;
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
 * 数据点位控制器测试类
 */
@WebMvcTest(
        controllers = DataPointController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("数据点位控制器测试")
class DataPointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataPointService dataPointService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID pointId;
    private DataPoint dataPoint;
    private DeviceRunningData runningData;

    @BeforeEach
    void setUp() {
        pointId = UUID.randomUUID();

        dataPoint = new DataPoint();
        dataPoint.setId(pointId);
        dataPoint.setPointCode("P001");
        dataPoint.setPointName("测试点位");
        dataPoint.setPointType(PointType.REAL);
        dataPoint.setDataType(MeasurementType.ANALOG);
        dataPoint.setUnit("MPa");
        dataPoint.setEnabled(true);

        runningData = new DeviceRunningData();
        runningData.setId(UUID.randomUUID());
        runningData.setDataPointId(pointId);
        runningData.setValue(100.5);
        runningData.setCollectionTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("获取所有点位 - 成功")
    void testFindAll_Success() throws Exception {
        // Given
        List<DataPoint> points = Arrays.asList(dataPoint);
        when(dataPointService.findAll()).thenReturn(points);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/data-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pointCode").value("P001"))
                .andExpect(jsonPath("$[0].pointName").value("测试点位"));

        verify(dataPointService, times(1)).findAll();
    }

    @Test
    @DisplayName("根据ID获取点位 - 成功")
    void testFindById_Success() throws Exception {
        // Given
        when(dataPointService.findById(pointId)).thenReturn(dataPoint);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/data-points/{id}", pointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointCode").value("P001"))
                .andExpect(jsonPath("$.pointName").value("测试点位"));

        verify(dataPointService, times(1)).findById(pointId);
    }

    @Test
    @DisplayName("创建点位 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        DataPointRequest request = new DataPointRequest();
        request.setPointCode("P002");
        request.setPointName("新点位");
        request.setPointType(PointType.REAL);
        request.setDataType(MeasurementType.ANALOG);
        request.setCollectionMode(CollectionMode.POLLING);
        request.setEnabled(true);

        DataPoint createdPoint = new DataPoint();
        createdPoint.setId(UUID.randomUUID());
        createdPoint.setPointCode("P002");
        createdPoint.setPointName("新点位");

        when(dataPointService.create(any(DataPointRequest.class))).thenReturn(createdPoint);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/data-points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointCode").value("P002"))
                .andExpect(jsonPath("$.pointName").value("新点位"));

        verify(dataPointService, times(1)).create(any(DataPointRequest.class));
    }

    @Test
    @DisplayName("更新点位 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        DataPointRequest request = new DataPointRequest();
        request.setPointCode("P001");
        request.setPointName("更新后的点位");
        request.setPointType(PointType.REAL);
        request.setEnabled(false);

        dataPoint.setPointName("更新后的点位");
        dataPoint.setEnabled(false);

        when(dataPointService.update(eq(pointId), any(DataPointRequest.class))).thenReturn(dataPoint);

        // When & Then
        mockMvc.perform(put("/api/remote-monitoring/data-points/{id}", pointId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointName").value("更新后的点位"));

        verify(dataPointService, times(1)).update(eq(pointId), any(DataPointRequest.class));
    }

    @Test
    @DisplayName("删除点位 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(dataPointService).delete(pointId);

        // When & Then
        mockMvc.perform(delete("/api/remote-monitoring/data-points/{id}", pointId))
                .andExpect(status().isNoContent());

        verify(dataPointService, times(1)).delete(pointId);
    }

    @Test
    @DisplayName("手动录入数据 - 成功")
    void testInputManualData_Success() throws Exception {
        // Given
        ManualDataInputRequest request = new ManualDataInputRequest();
        request.setDataPointId(pointId);
        request.setValue(100.5);
        request.setInputById(UUID.randomUUID());
        request.setInputByName("测试用户");
        request.setCollectionTime(LocalDateTime.now());

        when(dataPointService.inputManualData(any(ManualDataInputRequest.class))).thenReturn(runningData);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/data-points/manual-input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(100.5));

        verify(dataPointService, times(1)).inputManualData(any(ManualDataInputRequest.class));
    }

    @Test
    @DisplayName("根据点位类型查找 - 成功")
    void testFindByPointType_Success() throws Exception {
        // Given
        List<DataPoint> points = Arrays.asList(dataPoint);
        when(dataPointService.findByPointType(PointType.REAL)).thenReturn(points);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/data-points/type/REAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pointType").value("REAL"));

        verify(dataPointService, times(1)).findByPointType(PointType.REAL);
    }
}


