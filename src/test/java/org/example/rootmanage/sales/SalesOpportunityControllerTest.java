package org.example.rootmanage.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.sales.dto.*;
import org.example.rootmanage.sales.entity.SalesOpportunity;
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

import java.math.BigDecimal;
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
 * 销售机会管理Controller测试类
 */
@WebMvcTest(
        controllers = SalesController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("销售机会管理Controller测试")
class SalesOpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesService salesService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID opportunityId;
    private UUID customerId;
    private SalesOpportunity opportunity;
    private SalesOpportunityRequest opportunityRequest;

    @BeforeEach
    void setUp() {
        opportunityId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        opportunity = new SalesOpportunity();
        opportunity.setId(opportunityId);
        opportunity.setOpportunityName("测试机会");
        opportunity.setOpportunitySubject("测试主题");
        opportunity.setOpportunityDate(LocalDate.now());
        opportunity.setBudget(new BigDecimal("100000"));
        opportunity.setReceived(false);
        opportunity.setSubmitted(false);

        opportunityRequest = new SalesOpportunityRequest();
        opportunityRequest.setCustomerId(customerId);
        opportunityRequest.setOpportunityName("测试机会");
        opportunityRequest.setOpportunitySubject("测试主题");
        opportunityRequest.setOpportunityDate(LocalDate.now());
        opportunityRequest.setBudget(new BigDecimal("100000"));
    }

    @Test
    @DisplayName("查询销售机会列表 - 成功")
    void testGetSalesOpportunities_Success() throws Exception {
        List<SalesOpportunity> opportunities = Arrays.asList(opportunity);
        when(salesService.findAllSalesOpportunities(anyString())).thenReturn(opportunities);

        mockMvc.perform(get("/api/sales/opportunities")
                        .param("keyword", "测试")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(opportunityId.toString()))
                .andExpect(jsonPath("$[0].opportunityName").value("测试机会"));

        verify(salesService, times(1)).findAllSalesOpportunities("测试");
    }

    @Test
    @DisplayName("创建销售机会 - 成功")
    void testCreateSalesOpportunity_Success() throws Exception {
        when(salesService.createSalesOpportunity(any(SalesOpportunityRequest.class))).thenReturn(opportunity);

        mockMvc.perform(post("/api/sales/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunityId.toString()))
                .andExpect(jsonPath("$.opportunityName").value("测试机会"));

        verify(salesService, times(1)).createSalesOpportunity(any(SalesOpportunityRequest.class));
    }

    @Test
    @DisplayName("创建销售机会 - 验证失败")
    void testCreateSalesOpportunity_ValidationFailed() throws Exception {
        SalesOpportunityRequest invalidRequest = new SalesOpportunityRequest(); // Missing customerId, opportunityName

        mockMvc.perform(post("/api/sales/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(salesService, never()).createSalesOpportunity(any(SalesOpportunityRequest.class));
    }

    @Test
    @DisplayName("更新销售机会 - 成功")
    void testUpdateSalesOpportunity_Success() throws Exception {
        SalesOpportunity updatedOpportunity = new SalesOpportunity();
        updatedOpportunity.setId(opportunityId);
        updatedOpportunity.setOpportunityName("更新后的机会");
        when(salesService.updateSalesOpportunity(eq(opportunityId), any(SalesOpportunityRequest.class)))
                .thenReturn(updatedOpportunity);

        opportunityRequest.setOpportunityName("更新后的机会");

        mockMvc.perform(put("/api/sales/opportunities/{id}", opportunityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunityId.toString()))
                .andExpect(jsonPath("$.opportunityName").value("更新后的机会"));

        verify(salesService, times(1)).updateSalesOpportunity(eq(opportunityId), any(SalesOpportunityRequest.class));
    }

    @Test
    @DisplayName("删除销售机会 - 成功")
    void testDeleteSalesOpportunity_Success() throws Exception {
        doNothing().when(salesService).deleteSalesOpportunity(opportunityId);

        mockMvc.perform(delete("/api/sales/opportunities/{id}", opportunityId))
                .andExpect(status().isOk());

        verify(salesService, times(1)).deleteSalesOpportunity(opportunityId);
    }

    @Test
    @DisplayName("提交销售机会 - 成功")
    void testSubmitSalesOpportunity_Success() throws Exception {
        opportunity.setSubmitted(true);
        when(salesService.submitSalesOpportunity(opportunityId)).thenReturn(opportunity);

        mockMvc.perform(post("/api/sales/opportunities/{id}/submit", opportunityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunityId.toString()));

        verify(salesService, times(1)).submitSalesOpportunity(opportunityId);
    }

    @Test
    @DisplayName("传递销售机会 - 成功")
    void testTransferSalesOpportunity_Success() throws Exception {
        OpportunityTransferRequest request = new OpportunityTransferRequest();
        request.setOpportunityId(opportunityId);
        request.setTargetAreaIds(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        doNothing().when(salesService).transferSalesOpportunity(any(OpportunityTransferRequest.class));

        mockMvc.perform(post("/api/sales/opportunities/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(salesService, times(1)).transferSalesOpportunity(any(OpportunityTransferRequest.class));
    }

    @Test
    @DisplayName("分配员工 - 成功")
    void testAssignSalesOpportunity_Success() throws Exception {
        OpportunityAssignRequest request = new OpportunityAssignRequest();
        request.setOpportunityId(opportunityId);
        request.setPersonnelIds(Arrays.asList(UUID.randomUUID()));

        doNothing().when(salesService).assignSalesOpportunity(any(OpportunityAssignRequest.class));

        mockMvc.perform(post("/api/sales/opportunities/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(salesService, times(1)).assignSalesOpportunity(any(OpportunityAssignRequest.class));
    }

    @Test
    @DisplayName("关闭机会 - 成功")
    void testCloseSalesOpportunity_Success() throws Exception {
        OpportunityCloseRequest request = new OpportunityCloseRequest();
        request.setOpportunityId(opportunityId);
        request.setCloseReason("客户取消项目");

        opportunity.setCloseReason("客户取消项目");
        when(salesService.closeSalesOpportunity(any(OpportunityCloseRequest.class))).thenReturn(opportunity);

        mockMvc.perform(post("/api/sales/opportunities/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(opportunityId.toString()));

        verify(salesService, times(1)).closeSalesOpportunity(any(OpportunityCloseRequest.class));
    }

    @Test
    @DisplayName("查询待分配机会 - 成功")
    void testGetUnassignedOpportunities_Success() throws Exception {
        List<SalesOpportunity> opportunities = Arrays.asList(opportunity);
        when(salesService.findUnassignedOpportunities()).thenReturn(opportunities);

        mockMvc.perform(get("/api/sales/opportunities/unassigned")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(opportunityId.toString()));

        verify(salesService, times(1)).findUnassignedOpportunities();
    }
}

