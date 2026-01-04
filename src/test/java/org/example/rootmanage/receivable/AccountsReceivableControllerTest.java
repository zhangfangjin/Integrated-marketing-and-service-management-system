package org.example.rootmanage.receivable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.receivable.dto.AccountsReceivableResponse;
import org.example.rootmanage.receivable.dto.AccountsReceivableUpdateRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 应收账管理Controller测试类
 */
@WebMvcTest(
        controllers = AccountsReceivableController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("应收账管理Controller测试")
class AccountsReceivableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsReceivableService accountsReceivableService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID receivableId;
    private AccountsReceivableResponse receivableResponse;
    private AccountsReceivableUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        receivableId = UUID.randomUUID();

        // 创建测试应收账响应DTO
        receivableResponse = new AccountsReceivableResponse();
        receivableResponse.setId(receivableId);
        receivableResponse.setContractNumber("CT001");
        receivableResponse.setContractName("测试合同");
        receivableResponse.setCustomerName("测试客户");
        receivableResponse.setPaymentStage("第一阶段");
        receivableResponse.setAmountPayable(100000.00);
        receivableResponse.setAmountPaid(50000.00);
        receivableResponse.setUnpaidAmount(50000.00);
        receivableResponse.setPaymentStageName("预付款");
        receivableResponse.setDueDate(Date.valueOf("2024-06-30"));
        receivableResponse.setPaymentDate(Date.valueOf("2024-06-15"));
        receivableResponse.setResponsiblePerson("张三");
        receivableResponse.setRemark("测试备注");

        // 创建测试更新请求DTO
        updateRequest = new AccountsReceivableUpdateRequest();
        updateRequest.setPaymentStage("第一阶段");
        updateRequest.setPaymentStageName("预付款");
        updateRequest.setAmountPayable(100000.00);
        updateRequest.setAmountPaid(50000.00);
        updateRequest.setDueDate(Date.valueOf("2024-06-30"));
        updateRequest.setPaymentDate(Date.valueOf("2024-06-15"));
        updateRequest.setResponsiblePerson("张三");
        updateRequest.setRemark("测试备注");
    }

    @Test
    @DisplayName("查询应收账计划列表 - 成功")
    void testGetPlan_Success() throws Exception {
        // 准备数据
        List<AccountsReceivableResponse> responses = Arrays.asList(receivableResponse);
        when(accountsReceivableService.findAll()).thenReturn(responses);

        // 执行请求
        mockMvc.perform(get("/api/receivable/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(receivableId.toString()))
                .andExpect(jsonPath("$[0].contractNumber").value("CT001"))
                .andExpect(jsonPath("$[0].contractName").value("测试合同"))
                .andExpect(jsonPath("$[0].customerName").value("测试客户"))
                .andExpect(jsonPath("$[0].amountPayable").value(100000.00))
                .andExpect(jsonPath("$[0].amountPaid").value(50000.00))
                .andExpect(jsonPath("$[0].unpaidAmount").value(50000.00));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findAll();
    }

    @Test
    @DisplayName("查询应收账计划列表 - 带关键词搜索")
    void testGetPlan_WithKeyword() throws Exception {
        // 准备数据
        List<AccountsReceivableResponse> responses = Arrays.asList(receivableResponse);
        when(accountsReceivableService.search("CT001")).thenReturn(responses);

        // 执行请求
        mockMvc.perform(get("/api/receivable/plan")
                        .param("keyword", "CT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contractNumber").value("CT001"));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).search("CT001");
    }

    @Test
    @DisplayName("根据合同编号查询应收账计划 - 成功")
    void testGetPlanByContract_Success() throws Exception {
        // 准备数据
        List<AccountsReceivableResponse> responses = Arrays.asList(receivableResponse);
        when(accountsReceivableService.findByContractNumber("CT001")).thenReturn(responses);

        // 执行请求
        mockMvc.perform(get("/api/receivable/plan/contract/{contractNumber}", "CT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contractNumber").value("CT001"));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findByContractNumber("CT001");
    }

    @Test
    @DisplayName("查询应收账列表 - 成功")
    void testSearch_Success() throws Exception {
        // 准备数据
        List<AccountsReceivableResponse> responses = Arrays.asList(receivableResponse);
        when(accountsReceivableService.findAll()).thenReturn(responses);

        // 执行请求
        mockMvc.perform(get("/api/receivable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(receivableId.toString()));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findAll();
    }

    @Test
    @DisplayName("查询未付清应收账 - 成功")
    void testGetUnpaid_Success() throws Exception {
        // 准备数据
        List<AccountsReceivableResponse> responses = Arrays.asList(receivableResponse);
        when(accountsReceivableService.findUnpaid()).thenReturn(responses);

        // 执行请求
        mockMvc.perform(get("/api/receivable/unpaid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].unpaidAmount").value(50000.00));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findUnpaid();
    }

    @Test
    @DisplayName("根据ID获取应收账详情 - 成功")
    void testGetById_Success() throws Exception {
        // 准备数据
        when(accountsReceivableService.findById(receivableId)).thenReturn(receivableResponse);

        // 执行请求
        mockMvc.perform(get("/api/receivable/{id}", receivableId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(receivableId.toString()))
                .andExpect(jsonPath("$.contractNumber").value("CT001"))
                .andExpect(jsonPath("$.amountPayable").value(100000.00))
                .andExpect(jsonPath("$.amountPaid").value(50000.00))
                .andExpect(jsonPath("$.unpaidAmount").value(50000.00));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findById(receivableId);
    }

    @Test
    @DisplayName("根据ID获取应收账详情 - 不存在")
    void testGetById_NotFound() throws Exception {
        // 准备数据
        when(accountsReceivableService.findById(receivableId))
                .thenThrow(new IllegalArgumentException("应收账记录不存在"));

        // 执行请求
        mockMvc.perform(get("/api/receivable/{id}", receivableId))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).findById(receivableId);
    }

    @Test
    @DisplayName("更新应收账 - 成功")
    void testUpdate_Success() throws Exception {
        // 准备数据
        receivableResponse.setAmountPaid(80000.00);
        receivableResponse.setUnpaidAmount(20000.00);
        updateRequest.setAmountPaid(80000.00);
        when(accountsReceivableService.update(eq(receivableId), any(AccountsReceivableUpdateRequest.class)))
                .thenReturn(receivableResponse);

        // 执行请求
        mockMvc.perform(put("/api/receivable/{id}", receivableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(receivableId.toString()))
                .andExpect(jsonPath("$.amountPaid").value(80000.00))
                .andExpect(jsonPath("$.unpaidAmount").value(20000.00));

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).update(eq(receivableId), any(AccountsReceivableUpdateRequest.class));
    }

    @Test
    @DisplayName("更新应收账 - 应收账不存在")
    void testUpdate_NotFound() throws Exception {
        // 准备数据
        when(accountsReceivableService.update(eq(receivableId), any(AccountsReceivableUpdateRequest.class)))
                .thenThrow(new IllegalArgumentException("应收账记录不存在"));

        // 执行请求
        mockMvc.perform(put("/api/receivable/{id}", receivableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(accountsReceivableService, times(1)).update(eq(receivableId), any(AccountsReceivableUpdateRequest.class));
    }
}

