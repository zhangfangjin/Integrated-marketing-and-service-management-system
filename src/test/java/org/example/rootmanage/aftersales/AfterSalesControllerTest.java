package org.example.rootmanage.aftersales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.aftersales.dto.*;
import org.example.rootmanage.aftersales.entity.AfterSalesOrder;
import org.example.rootmanage.aftersales.entity.AfterSalesType;
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
 * 售后服务控制器测试类
 */
@WebMvcTest(
        controllers = AfterSalesController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("售后服务控制器测试")
class AfterSalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AfterSalesService afterSalesService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID orderId;
    private AfterSalesOrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        orderResponse = new AfterSalesOrderResponse();
        orderResponse.setId(orderId);
        orderResponse.setServiceOrderNumber("AS20240101001");
        orderResponse.setServiceType(AfterSalesType.TROUBLESHOOTING);
        orderResponse.setCustomerUnit("测试客户");
        orderResponse.setServiceStatus("待分配");
    }

    @Test
    @DisplayName("获取售后服务单列表 - 成功")
    void testList_Success() throws Exception {
        // Given
        List<AfterSalesOrderResponse> orders = Arrays.asList(orderResponse);
        when(afterSalesService.findAll()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/after-sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceOrderNumber").value("AS20240101001"))
                .andExpect(jsonPath("$[0].customerUnit").value("测试客户"));

        verify(afterSalesService, times(1)).findAll();
    }

    @Test
    @DisplayName("获取售后服务单列表 - 带关键词搜索")
    void testList_WithKeyword() throws Exception {
        // Given
        List<AfterSalesOrderResponse> orders = Arrays.asList(orderResponse);
        when(afterSalesService.searchAfterSalesOrders("测试")).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/after-sales")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerUnit").value("测试客户"));

        verify(afterSalesService, times(1)).searchAfterSalesOrders("测试");
    }

    @Test
    @DisplayName("根据ID获取售后服务单 - 成功")
    void testGetById_Success() throws Exception {
        // Given
        when(afterSalesService.findById(orderId)).thenReturn(orderResponse);

        // When & Then
        mockMvc.perform(get("/api/after-sales/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceOrderNumber").value("AS20240101001"))
                .andExpect(jsonPath("$.customerUnit").value("测试客户"));

        verify(afterSalesService, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("创建售后服务单 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        AfterSalesOrderRequest request = new AfterSalesOrderRequest();
        request.setServiceType(AfterSalesType.TROUBLESHOOTING);
        request.setContractId(UUID.randomUUID());
        request.setHandlerName("新客户");
        request.setServiceDate(Date.valueOf(LocalDate.now()));

        AfterSalesOrder order = new AfterSalesOrder();
        order.setId(UUID.randomUUID());
        order.setServiceOrderNumber("AS20240101002");

        when(afterSalesService.create(any(AfterSalesOrderRequest.class))).thenReturn(AfterSalesOrderResponse.fromEntity(order));

        // When & Then
        mockMvc.perform(post("/api/after-sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceOrderNumber").value("AS20240101002"));

        verify(afterSalesService, times(1)).create(any(AfterSalesOrderRequest.class));
    }

    @Test
    @DisplayName("指派受理人员 - 成功")
    void testAssignHandler_Success() throws Exception {
        // Given
        AssignHandlerRequest request = new AssignHandlerRequest();
        UUID handlerId = UUID.randomUUID();
        request.setHandlerId(handlerId);
        request.setHandlerName("测试处理人");

        AfterSalesOrder order = new AfterSalesOrder();
        order.setId(orderId);
        order.setHandlerId(handlerId);
        order.setHandlerName("测试处理人");

        when(afterSalesService.assignHandler(eq(orderId), any(AssignHandlerRequest.class)))
                .thenReturn(AfterSalesOrderResponse.fromEntity(order));

        // When & Then
        mockMvc.perform(post("/api/after-sales/{id}/assign-handler", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handlerName").value("测试处理人"));

        verify(afterSalesService, times(1))
                .assignHandler(eq(orderId), any(AssignHandlerRequest.class));
    }

    @Test
    @DisplayName("完成售后服务 - 成功")
    void testCompleteService_Success() throws Exception {
        // Given
        CompleteServiceRequest request = new CompleteServiceRequest();
        request.setDeviceNumber("DEV001");
        request.setDeviceName("测试设备");
        request.setServiceSummary("服务完成");

        AfterSalesOrder order = new AfterSalesOrder();
        order.setId(orderId);
        order.setDeviceNumber("DEV001");
        order.setServiceStatus("已完成");

        when(afterSalesService.completeService(eq(orderId), any(CompleteServiceRequest.class)))
                .thenReturn(AfterSalesOrderResponse.fromEntity(order));

        // When & Then
        mockMvc.perform(post("/api/after-sales/{id}/complete", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceNumber").value("DEV001"))
                .andExpect(jsonPath("$.serviceStatus").value("已完成"));

        verify(afterSalesService, times(1))
                .completeService(eq(orderId), any(CompleteServiceRequest.class));
    }

    @Test
    @DisplayName("评价售后服务 - 成功")
    void testEvaluateService_Success() throws Exception {
        // Given
        EvaluateServiceRequest request = new EvaluateServiceRequest();
        request.setEvaluation("优");
        request.setEvaluatorName("测试评价人");

        AfterSalesOrder order = new AfterSalesOrder();
        order.setId(orderId);
        order.setEvaluation("优");
        order.setIsClosed(true);

        when(afterSalesService.evaluateService(eq(orderId), any(EvaluateServiceRequest.class)))
                .thenReturn(AfterSalesOrderResponse.fromEntity(order));

        // When & Then
        mockMvc.perform(post("/api/after-sales/{id}/evaluate", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluation").value("优"));

        verify(afterSalesService, times(1))
                .evaluateService(eq(orderId), any(EvaluateServiceRequest.class));
    }
}


