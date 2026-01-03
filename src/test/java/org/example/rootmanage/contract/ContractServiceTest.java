package org.example.rootmanage.contract;

import org.example.rootmanage.contract.dto.ContractExecutionProgressRequest;
import org.example.rootmanage.contract.dto.ContractExecutionProgressResponse;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractExecutionProgress;
import org.example.rootmanage.contract.repository.ContractApprovalNodeRepository;
import org.example.rootmanage.contract.repository.ContractDetailRepository;
import org.example.rootmanage.contract.repository.ContractExecutionProgressRepository;
import org.example.rootmanage.contract.repository.ContractPaymentStageRepository;
import org.example.rootmanage.contract.repository.ContractProportionRepository;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.example.rootmanage.contract.repository.ContractWorkflowStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 合同服务测试类
 * 测试合同执行进度相关功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("合同服务测试")
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractDetailRepository contractDetailRepository;

    @Mock
    private ContractPaymentStageRepository contractPaymentStageRepository;

    @Mock
    private ContractProportionRepository contractProportionRepository;

    @Mock
    private ContractApprovalNodeRepository contractApprovalNodeRepository;

    @Mock
    private ContractWorkflowStatusRepository contractWorkflowStatusRepository;

    @Mock
    private ContractExecutionProgressRepository contractExecutionProgressRepository;

    @InjectMocks
    private ContractService contractService;

    private UUID contractId;
    private Contract contract;
    private ContractExecutionProgress progress;

    @BeforeEach
    void setUp() {
        contractId = UUID.randomUUID();
        
        // 创建测试合同
        contract = new Contract();
        contract.setId(contractId);
        contract.setContractNumber("CT001");
        contract.setContractName("测试合同");
        contract.setCustomerName("测试客户");

        // 创建测试执行进度
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
    @DisplayName("查询合同执行进度 - 存在进度记录")
    void testGetExecutionProgress_WhenProgressExists() {
        // Given
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractExecutionProgressRepository.findByContractId(contractId))
                .thenReturn(Optional.of(progress));

        // When
        ContractExecutionProgress result = contractService.getExecutionProgress(contractId);

        // Then
        assertNotNull(result);
        assertEquals("已完成", result.getDesignProgress());
        assertEquals("进行中", result.getProductionProgress());
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, times(1)).findByContractId(contractId);
    }

    @Test
    @DisplayName("查询合同执行进度 - 不存在进度记录，返回默认空记录")
    void testGetExecutionProgress_WhenProgressNotExists() {
        // Given
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractExecutionProgressRepository.findByContractId(contractId))
                .thenReturn(Optional.empty());

        // When
        ContractExecutionProgress result = contractService.getExecutionProgress(contractId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getContract());
        assertEquals(contractId, result.getContract().getId());
        assertNull(result.getDesignProgress());
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, times(1)).findByContractId(contractId);
    }

    @Test
    @DisplayName("查询合同执行进度 - 合同不存在，抛出异常")
    void testGetExecutionProgress_WhenContractNotExists() {
        // Given
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            contractService.getExecutionProgress(contractId);
        });
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, never()).findByContractId(any());
    }

    @Test
    @DisplayName("更新合同执行进度 - 存在进度记录")
    void testUpdateExecutionProgress_WhenProgressExists() {
        // Given
        ContractExecutionProgressRequest request = new ContractExecutionProgressRequest();
        request.setDesignProgress("已完成");
        request.setProductionProgress("已完成");
        request.setProcurementProgress("进行中");
        request.setManufacturingProgress("未完成");
        request.setAssemblyProgress("未完成");
        request.setRemark("测试备注");

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractExecutionProgressRepository.findByContractId(contractId))
                .thenReturn(Optional.of(progress));
        when(contractExecutionProgressRepository.save(any(ContractExecutionProgress.class)))
                .thenReturn(progress);

        // When
        ContractExecutionProgress result = contractService.updateExecutionProgress(contractId, request);

        // Then
        assertNotNull(result);
        assertEquals("已完成", result.getDesignProgress());
        assertEquals("已完成", result.getProductionProgress());
        assertEquals("进行中", result.getProcurementProgress());
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, times(1)).findByContractId(contractId);
        verify(contractExecutionProgressRepository, times(1)).save(any(ContractExecutionProgress.class));
    }

    @Test
    @DisplayName("更新合同执行进度 - 不存在进度记录，创建新记录")
    void testUpdateExecutionProgress_WhenProgressNotExists() {
        // Given
        ContractExecutionProgressRequest request = new ContractExecutionProgressRequest();
        request.setDesignProgress("已完成");
        request.setProductionProgress("进行中");

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractExecutionProgressRepository.findByContractId(contractId))
                .thenReturn(Optional.empty());
        when(contractExecutionProgressRepository.save(any(ContractExecutionProgress.class)))
                .thenAnswer(invocation -> {
                    ContractExecutionProgress saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        // When
        ContractExecutionProgress result = contractService.updateExecutionProgress(contractId, request);

        // Then
        assertNotNull(result);
        assertEquals("已完成", result.getDesignProgress());
        assertEquals("进行中", result.getProductionProgress());
        assertEquals(contract, result.getContract());
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, times(1)).findByContractId(contractId);
        verify(contractExecutionProgressRepository, times(1)).save(any(ContractExecutionProgress.class));
    }

    @Test
    @DisplayName("更新合同执行进度 - 合同不存在，抛出异常")
    void testUpdateExecutionProgress_WhenContractNotExists() {
        // Given
        ContractExecutionProgressRequest request = new ContractExecutionProgressRequest();
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            contractService.updateExecutionProgress(contractId, request);
        });
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractExecutionProgressRepository, never()).findByContractId(any());
        verify(contractExecutionProgressRepository, never()).save(any());
    }

    @Test
    @DisplayName("查询所有合同的执行进度列表 - 包含有进度和无进度的合同")
    void testGetAllExecutionProgress() {
        // Given
        Contract contract1 = new Contract();
        contract1.setId(UUID.randomUUID());
        contract1.setContractNumber("CT001");
        contract1.setContractName("合同1");

        Contract contract2 = new Contract();
        contract2.setId(UUID.randomUUID());
        contract2.setContractNumber("CT002");
        contract2.setContractName("合同2");

        ContractExecutionProgress progress1 = new ContractExecutionProgress();
        progress1.setContract(contract1);
        progress1.setDesignProgress("已完成");
        progress1.setProductionProgress("进行中");

        List<Contract> allContracts = Arrays.asList(contract1, contract2);

        when(contractRepository.findAll()).thenReturn(allContracts);
        when(contractExecutionProgressRepository.findByContractId(contract1.getId()))
                .thenReturn(Optional.of(progress1));
        when(contractExecutionProgressRepository.findByContractId(contract2.getId()))
                .thenReturn(Optional.empty());

        // When
        List<ContractExecutionProgressResponse> result = contractService.getAllExecutionProgress();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // 验证第一个合同（有进度记录）
        ContractExecutionProgressResponse response1 = result.get(0);
        assertEquals(contract1.getId(), response1.getContractId());
        assertEquals("CT001", response1.getContractNumber());
        assertEquals("合同1", response1.getContractName());
        assertEquals("已完成", response1.getDesignProgress());
        assertEquals("进行中", response1.getProductionProgress());

        // 验证第二个合同（无进度记录）
        ContractExecutionProgressResponse response2 = result.get(1);
        assertEquals(contract2.getId(), response2.getContractId());
        assertEquals("CT002", response2.getContractNumber());
        assertEquals("合同2", response2.getContractName());
        assertNull(response2.getDesignProgress());
        assertNull(response2.getProductionProgress());

        verify(contractRepository, times(1)).findAll();
        verify(contractExecutionProgressRepository, times(2)).findByContractId(any());
    }

    @Test
    @DisplayName("查询所有合同的执行进度列表 - 空列表")
    void testGetAllExecutionProgress_WhenNoContracts() {
        // Given
        when(contractRepository.findAll()).thenReturn(List.of());

        // When
        List<ContractExecutionProgressResponse> result = contractService.getAllExecutionProgress();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contractRepository, times(1)).findAll();
        verify(contractExecutionProgressRepository, never()).findByContractId(any());
    }
}

