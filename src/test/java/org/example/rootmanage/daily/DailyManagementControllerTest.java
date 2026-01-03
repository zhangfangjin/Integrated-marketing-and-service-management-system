package org.example.rootmanage.daily;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.daily.dto.DestinationManagementRequest;
import org.example.rootmanage.daily.dto.WeeklyReportRequest;
import org.example.rootmanage.daily.entity.DestinationManagement;
import org.example.rootmanage.daily.entity.WeeklyReport;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 日常管理Controller测试类
 */
@WebMvcTest(
        controllers = DailyManagementController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("日常管理Controller测试")
class DailyManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyManagementService dailyManagementService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID destinationId;
    private UUID reportId;
    private UUID userId;
    private DestinationManagement destination;
    private WeeklyReport report;
    private DestinationManagementRequest destinationRequest;
    private WeeklyReportRequest reportRequest;

    @BeforeEach
    void setUp() {
        destinationId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        userId = UUID.randomUUID();

        destination = new DestinationManagement();
        destination.setId(destinationId);
        destination.setActivityName("测试活动");
        destination.setLocation("测试地点");
        destination.setTime(LocalDateTime.now());
        destination.setSubmitted(false);

        report = new WeeklyReport();
        report.setId(reportId);
        report.setReportName("工作汇报");
        report.setReportTime(LocalDateTime.now());
        report.setContent("本周工作总结");
        report.setRemark("无");
        report.setSubmitted(false);

        destinationRequest = new DestinationManagementRequest();
        destinationRequest.setActivityName("测试活动");
        destinationRequest.setLocation("测试地点");
        destinationRequest.setTime(LocalDateTime.now());
        destinationRequest.setUserId(userId);

        reportRequest = new WeeklyReportRequest();
        reportRequest.setReportName("工作汇报");
        reportRequest.setReportTime(LocalDateTime.now());
        reportRequest.setContent("本周工作总结");
        reportRequest.setRemark("无");
        reportRequest.setUserId(userId);
    }

    // ========== 去向管理测试 ==========

    @Test
    @DisplayName("查询去向信息列表 - 成功")
    void testGetDestinations_Success() throws Exception {
        List<DestinationManagement> destinations = Arrays.asList(destination);
        when(dailyManagementService.findAllDestinations(null, null)).thenReturn(destinations);

        mockMvc.perform(get("/api/daily-management/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(destinationId.toString()));

        verify(dailyManagementService, times(1)).findAllDestinations(null, null);
    }

    @Test
    @DisplayName("根据ID查询去向信息 - 成功")
    void testGetDestinationById_Success() throws Exception {
        when(dailyManagementService.findDestinationById(destinationId)).thenReturn(Optional.of(destination));

        mockMvc.perform(get("/api/daily-management/destinations/{id}", destinationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(destinationId.toString()));

        verify(dailyManagementService, times(1)).findDestinationById(destinationId);
    }

    @Test
    @DisplayName("创建去向信息 - 成功")
    void testCreateDestination_Success() throws Exception {
        when(dailyManagementService.createDestination(any(DestinationManagementRequest.class))).thenReturn(destination);

        mockMvc.perform(post("/api/daily-management/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(destinationId.toString()));

        verify(dailyManagementService, times(1)).createDestination(any(DestinationManagementRequest.class));
    }

    @Test
    @DisplayName("更新去向信息 - 成功")
    void testUpdateDestination_Success() throws Exception {
        when(dailyManagementService.updateDestination(eq(destinationId), any(DestinationManagementRequest.class)))
                .thenReturn(destination);

        mockMvc.perform(put("/api/daily-management/destinations/{id}", destinationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinationRequest)))
                .andExpect(status().isOk());

        verify(dailyManagementService, times(1)).updateDestination(eq(destinationId), any(DestinationManagementRequest.class));
    }

    @Test
    @DisplayName("提交去向信息 - 成功")
    void testSubmitDestination_Success() throws Exception {
        destination.setSubmitted(true);
        when(dailyManagementService.submitDestination(destinationId)).thenReturn(destination);

        mockMvc.perform(post("/api/daily-management/destinations/{id}/submit", destinationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submitted").value(true));

        verify(dailyManagementService, times(1)).submitDestination(destinationId);
    }

    @Test
    @DisplayName("删除去向信息 - 成功")
    void testDeleteDestination_Success() throws Exception {
        doNothing().when(dailyManagementService).deleteDestination(destinationId);

        mockMvc.perform(delete("/api/daily-management/destinations/{id}", destinationId))
                .andExpect(status().isNoContent());

        verify(dailyManagementService, times(1)).deleteDestination(destinationId);
    }

    // ========== 周报管理测试 ==========

    @Test
    @DisplayName("查询周报列表 - 成功")
    void testGetWeeklyReports_Success() throws Exception {
        List<WeeklyReport> reports = Arrays.asList(report);
        when(dailyManagementService.findAllWeeklyReports(null, null)).thenReturn(reports);

        mockMvc.perform(get("/api/daily-management/weekly-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(reportId.toString()));

        verify(dailyManagementService, times(1)).findAllWeeklyReports(null, null);
    }

    @Test
    @DisplayName("根据ID查询周报 - 成功")
    void testGetWeeklyReportById_Success() throws Exception {
        when(dailyManagementService.findWeeklyReportById(reportId)).thenReturn(Optional.of(report));

        mockMvc.perform(get("/api/daily-management/weekly-reports/{id}", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));

        verify(dailyManagementService, times(1)).findWeeklyReportById(reportId);
    }

    @Test
    @DisplayName("创建周报 - 成功")
    void testCreateWeeklyReport_Success() throws Exception {
        when(dailyManagementService.createWeeklyReport(any(WeeklyReportRequest.class))).thenReturn(report);

        mockMvc.perform(post("/api/daily-management/weekly-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reportId.toString()));

        verify(dailyManagementService, times(1)).createWeeklyReport(any(WeeklyReportRequest.class));
    }

    @Test
    @DisplayName("更新周报 - 成功")
    void testUpdateWeeklyReport_Success() throws Exception {
        when(dailyManagementService.updateWeeklyReport(eq(reportId), any(WeeklyReportRequest.class)))
                .thenReturn(report);

        mockMvc.perform(put("/api/daily-management/weekly-reports/{id}", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportRequest)))
                .andExpect(status().isOk());

        verify(dailyManagementService, times(1)).updateWeeklyReport(eq(reportId), any(WeeklyReportRequest.class));
    }

    @Test
    @DisplayName("提交周报 - 成功")
    void testSubmitWeeklyReport_Success() throws Exception {
        report.setSubmitted(true);
        when(dailyManagementService.submitWeeklyReport(reportId)).thenReturn(report);

        mockMvc.perform(post("/api/daily-management/weekly-reports/{id}/submit", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submitted").value(true));

        verify(dailyManagementService, times(1)).submitWeeklyReport(reportId);
    }

    @Test
    @DisplayName("删除周报 - 成功")
    void testDeleteWeeklyReport_Success() throws Exception {
        doNothing().when(dailyManagementService).deleteWeeklyReport(reportId);

        mockMvc.perform(delete("/api/daily-management/weekly-reports/{id}", reportId))
                .andExpect(status().isNoContent());

        verify(dailyManagementService, times(1)).deleteWeeklyReport(reportId);
    }
}

