package org.example.rootmanage.aftersales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.aftersales.dto.MaintenancePlanRequest;
import org.example.rootmanage.aftersales.entity.MaintenancePlan;
import org.example.rootmanage.aftersales.entity.PlanType;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 维护计划控制器测试类
 */
@WebMvcTest(
        controllers = MaintenancePlanController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("维护计划控制器测试")
class MaintenancePlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaintenancePlanService maintenancePlanService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID planId;
    private UUID deviceId;
    private MaintenancePlan plan;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();
        deviceId = UUID.randomUUID();

        plan = new MaintenancePlan();
        plan.setId(planId);
        plan.setPlanType(PlanType.SPECIFIC_DEVICE);
        plan.setMaintenanceCycle("每3个月");
        plan.setMaintenanceItems("检查、清洁");
        plan.setEnabled(true);
    }

    @Test
    @DisplayName("获取维护计划列表 - 成功")
    void testList_Success() throws Exception {
        // Given
        List<MaintenancePlan> plans = Arrays.asList(plan);
        when(maintenancePlanService.findAll()).thenReturn(plans);

        // When & Then
        mockMvc.perform(get("/api/maintenance-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maintenanceCycle").value("每3个月"));

        verify(maintenancePlanService, times(1)).findAll();
    }

    @Test
    @DisplayName("根据设备ID获取维护计划 - 成功")
    void testFindByDeviceId_Success() throws Exception {
        // Given
        when(maintenancePlanService.findByDeviceId(deviceId)).thenReturn(plan);

        // When & Then
        mockMvc.perform(get("/api/maintenance-plans/device/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId.toString()));

        verify(maintenancePlanService, times(1)).findByDeviceId(deviceId);
    }

    @Test
    @DisplayName("创建维护计划 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        MaintenancePlanRequest request = new MaintenancePlanRequest();
        request.setPlanType(PlanType.SPECIFIC_DEVICE);
        request.setDeviceId(deviceId);
        request.setMaintenanceCycle("每6个月");
        request.setMaintenanceItems("全面检查");

        MaintenancePlan createdPlan = new MaintenancePlan();
        createdPlan.setId(UUID.randomUUID());
        createdPlan.setMaintenanceCycle("每6个月");

        when(maintenancePlanService.create(any(MaintenancePlanRequest.class)))
                .thenReturn(createdPlan);

        // When & Then
        mockMvc.perform(post("/api/maintenance-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceCycle").value("每6个月"));

        verify(maintenancePlanService, times(1))
                .create(any(MaintenancePlanRequest.class));
    }

    @Test
    @DisplayName("更新维护计划 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        MaintenancePlanRequest request = new MaintenancePlanRequest();
        request.setPlanType(PlanType.SPECIFIC_DEVICE);
        request.setDeviceId(deviceId);
        request.setMaintenanceCycle("每12个月");
        request.setMaintenanceItems("大修");

        plan.setMaintenanceCycle("每12个月");
        plan.setMaintenanceItems("大修");

        when(maintenancePlanService.update(eq(planId), any(MaintenancePlanRequest.class)))
                .thenReturn(plan);

        // When & Then
        mockMvc.perform(put("/api/maintenance-plans/{id}", planId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceCycle").value("每12个月"));

        verify(maintenancePlanService, times(1))
                .update(eq(planId), any(MaintenancePlanRequest.class));
    }

    @Test
    @DisplayName("删除维护计划 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(maintenancePlanService).delete(planId);

        // When & Then
        mockMvc.perform(delete("/api/maintenance-plans/{id}", planId))
                .andExpect(status().isOk());

        verify(maintenancePlanService, times(1)).delete(planId);
    }
}


