package org.example.rootmanage.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.contract.dto.ContractExecutionProgressRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressResponse;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractExecutionProgress;
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
 * 合同管理Controller测试类
 * 测试合同执行进度相关接口
 */
@WebMvcTest(
        controllers = ContractController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("合同管理Controller测试")
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID contractId;
    private Contract contract;
    private ContractExecutionProgress progress;

    @BeforeEach
    void setUp() {
        contractId = UUID.randomUUID();

        contract = new Contract();
        contract.setId(contractId);
        contract.setContractNumber("CT001");
        contract.setContractName("测试合同");

        progress = new ContractExecutionProgress();
        progress.setId(UUID.randomUUID());
        progress.setContract(contract);
        progress.setDesignProgress("已完成");
        progress.setProductionProgress("进行中");
        progress.setProcurementProgress("未完成");
        progress.setManufacturingProgress("未完成");
        progress.setAssemblyProgress("未完成");
    }

    @Test
    @DisplayName("查询合同执行进度 - 成功")
    void testGetExecutionProgress_Success() throws Exception {
        // Given
        when(contractService.getExecutionProgress(contractId)).thenReturn(progress);

        // When & Then
        mockMvc.perform(get("/api/contracts/{id}/execution-progress", contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designProgress").value("已完成"))
                .andExpect(jsonPath("$.productionProgress").value("进行中"))
                .andExpect(jsonPath("$.procurementProgress").value("未完成"));

        verify(contractService, times(1)).getExecutionProgress(contractId);
    }

    @Test
    @DisplayName("查询合同执行进度 - 合同不存在")
    void testGetExecutionProgress_ContractNotFound() throws Exception {
        // Given
        when(contractService.getExecutionProgress(contractId))
                .thenThrow(new IllegalArgumentException("合同不存在"));

        // When & Then
        mockMvc.perform(get("/api/contracts/{id}/execution-progress", contractId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("合同不存在"));

        verify(contractService, times(1)).getExecutionProgress(contractId);
    }

    @Test
    @DisplayName("更新合同执行进度 - 成功")
    void testUpdateExecutionProgress_Success() throws Exception {
        // Given
        ContractExecutionProgressRequest request = new ContractExecutionProgressRequest();
        request.setDesignProgress("已完成");
        request.setProductionProgress("已完成");
        request.setProcurementProgress("进行中");
        request.setManufacturingProgress("未完成");
        request.setAssemblyProgress("未完成");
        request.setRemark("更新备注");

        ContractExecutionProgress updatedProgress = new ContractExecutionProgress();
        updatedProgress.setId(UUID.randomUUID());
        updatedProgress.setContract(contract);
        updatedProgress.setDesignProgress("已完成");
        updatedProgress.setProductionProgress("已完成");
        updatedProgress.setProcurementProgress("进行中");
        updatedProgress.setManufacturingProgress("未完成");
        updatedProgress.setAssemblyProgress("未完成");
        updatedProgress.setRemark("更新备注");

        when(contractService.updateExecutionProgress(eq(contractId), any(ContractExecutionProgressRequest.class)))
                .thenReturn(updatedProgress);

        // When & Then
        mockMvc.perform(put("/api/contracts/{id}/execution-progress", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designProgress").value("已完成"))
                .andExpect(jsonPath("$.productionProgress").value("已完成"))
                .andExpect(jsonPath("$.procurementProgress").value("进行中"))
                .andExpect(jsonPath("$.remark").value("更新备注"));

        verify(contractService, times(1))
                .updateExecutionProgress(eq(contractId), any(ContractExecutionProgressRequest.class));
    }

    @Test
    @DisplayName("更新合同执行进度 - 合同不存在")
    void testUpdateExecutionProgress_ContractNotFound() throws Exception {
        // Given
        ContractExecutionProgressRequest request = new ContractExecutionProgressRequest();
        request.setDesignProgress("已完成");

        when(contractService.updateExecutionProgress(eq(contractId), any(ContractExecutionProgressRequest.class)))
                .thenThrow(new IllegalArgumentException("合同不存在"));

        // When & Then
        mockMvc.perform(put("/api/contracts/{id}/execution-progress", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("合同不存在"));

        verify(contractService, times(1))
                .updateExecutionProgress(eq(contractId), any(ContractExecutionProgressRequest.class));
    }

    @Test
    @DisplayName("查询所有合同的执行进度列表 - 成功")
    void testGetAllExecutionProgress_Success() throws Exception {
        // Given
        ContractExecutionProgressResponse response1 = new ContractExecutionProgressResponse();
        response1.setContractId(UUID.randomUUID());
        response1.setContractNumber("CT001");
        response1.setContractName("合同1");
        response1.setDesignProgress("已完成");
        response1.setProductionProgress("进行中");

        ContractExecutionProgressResponse response2 = new ContractExecutionProgressResponse();
        response2.setContractId(UUID.randomUUID());
        response2.setContractNumber("CT002");
        response2.setContractName("合同2");
        response2.setDesignProgress("未完成");
        response2.setProductionProgress("未完成");

        List<ContractExecutionProgressResponse> responses = Arrays.asList(response1, response2);

        when(contractService.getAllExecutionProgress()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/contracts/execution-progress/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].contractNumber").value("CT001"))
                .andExpect(jsonPath("$[0].contractName").value("合同1"))
                .andExpect(jsonPath("$[0].designProgress").value("已完成"))
                .andExpect(jsonPath("$[1].contractNumber").value("CT002"))
                .andExpect(jsonPath("$[1].contractName").value("合同2"))
                .andExpect(jsonPath("$[1].designProgress").value("未完成"));

        verify(contractService, times(1)).getAllExecutionProgress();
    }

    @Test
    @DisplayName("查询所有合同的执行进度列表 - 空列表")
    void testGetAllExecutionProgress_EmptyList() throws Exception {
        // Given
        when(contractService.getAllExecutionProgress()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/contracts/execution-progress/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService, times(1)).getAllExecutionProgress();
    }

    @Test
    @DisplayName("更新合同执行进度 - 请求体为空")
    void testUpdateExecutionProgress_EmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/contracts/{id}/execution-progress", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // Spring会创建空的请求对象

        verify(contractService, times(1))
                .updateExecutionProgress(eq(contractId), any(ContractExecutionProgressRequest.class));
    }
}

