package org.example.rootmanage.receivable;

import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.entity.ContractPaymentStage;
import org.example.rootmanage.contract.repository.ContractPaymentStageRepository;
import org.example.rootmanage.receivable.dto.AccountsReceivableResponse;
import org.example.rootmanage.receivable.dto.AccountsReceivableUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 应收账管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("应收账管理服务测试")
class AccountsReceivableServiceTest {

    @Mock
    private ContractPaymentStageRepository contractPaymentStageRepository;

    @InjectMocks
    private AccountsReceivableService accountsReceivableService;

    private UUID stageId;
    private UUID contractId;
    private Contract contract;
    private ContractPaymentStage paymentStage;
    private AccountsReceivableUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        stageId = UUID.randomUUID();
        contractId = UUID.randomUUID();

        // 创建测试合同
        contract = new Contract();
        contract.setId(contractId);
        contract.setContractNumber("CT001");
        contract.setContractName("测试合同");
        contract.setCustomerName("测试客户");

        // 创建测试付款阶段
        paymentStage = new ContractPaymentStage();
        paymentStage.setId(stageId);
        paymentStage.setContract(contract);
        paymentStage.setPaymentStage("第一阶段");
        paymentStage.setPaymentStageName("预付款");
        paymentStage.setAmountPayable(100000.0);
        paymentStage.setAmountPaid(50000.0);
        paymentStage.setDueDate(Date.valueOf("2024-06-30"));
        paymentStage.setPaymentDate(Date.valueOf("2024-06-15"));
        paymentStage.setResponsiblePerson("张三");
        paymentStage.setRemark("测试备注");

        // 创建测试更新请求
        updateRequest = new AccountsReceivableUpdateRequest();
        updateRequest.setPaymentStage("第一阶段");
        updateRequest.setPaymentStageName("预付款");
        updateRequest.setAmountPayable(100000.0);
        updateRequest.setAmountPaid(60000.0);
        updateRequest.setDueDate(Date.valueOf("2024-06-30"));
        updateRequest.setPaymentDate(Date.valueOf("2024-06-20"));
        updateRequest.setResponsiblePerson("李四");
        updateRequest.setRemark("更新备注");
    }

    @Test
    @DisplayName("查询所有应收账计划 - 成功")
    void testFindAll_Success() {
        // Given
        List<ContractPaymentStage> stages = Arrays.asList(paymentStage);
        when(contractPaymentStageRepository.findAll()).thenReturn(stages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        AccountsReceivableResponse response = result.get(0);
        assertEquals(stageId, response.getId());
        assertEquals("CT001", response.getContractNumber());
        assertEquals("测试合同", response.getContractName());
        assertEquals("测试客户", response.getCustomerName());
        assertEquals(100000.0, response.getAmountPayable());
        assertEquals(50000.0, response.getAmountPaid());
        assertEquals(50000.0, response.getUnpaidAmount());
        verify(contractPaymentStageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("根据合同编号查询应收账 - 成功")
    void testFindByContractNumber_Success() {
        // Given
        List<ContractPaymentStage> stages = Arrays.asList(paymentStage);
        when(contractPaymentStageRepository.findByContractNumber("CT001")).thenReturn(stages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.findByContractNumber("CT001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CT001", result.get(0).getContractNumber());
        verify(contractPaymentStageRepository, times(1)).findByContractNumber("CT001");
    }

    @Test
    @DisplayName("根据关键词搜索应收账 - 成功")
    void testSearch_Success() {
        // Given
        List<ContractPaymentStage> stages = Arrays.asList(paymentStage);
        when(contractPaymentStageRepository.searchByKeyword("%CT001%")).thenReturn(stages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.search("CT001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CT001", result.get(0).getContractNumber());
        verify(contractPaymentStageRepository, times(1)).searchByKeyword("%CT001%");
    }

    @Test
    @DisplayName("根据关键词搜索应收账 - 关键词为空，返回所有")
    void testSearch_EmptyKeyword() {
        // Given
        List<ContractPaymentStage> stages = Arrays.asList(paymentStage);
        when(contractPaymentStageRepository.findAll()).thenReturn(stages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.search(null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contractPaymentStageRepository, times(1)).findAll();
        verify(contractPaymentStageRepository, never()).searchByKeyword(any());
    }

    @Test
    @DisplayName("根据ID获取应收账详情 - 成功")
    void testFindById_Success() {
        // Given
        when(contractPaymentStageRepository.findById(stageId)).thenReturn(Optional.of(paymentStage));

        // When
        AccountsReceivableResponse result = accountsReceivableService.findById(stageId);

        // Then
        assertNotNull(result);
        assertEquals(stageId, result.getId());
        assertEquals("CT001", result.getContractNumber());
        assertEquals(100000.0, result.getAmountPayable());
        assertEquals(50000.0, result.getAmountPaid());
        assertEquals("张三", result.getResponsiblePerson());
        verify(contractPaymentStageRepository, times(1)).findById(stageId);
    }

    @Test
    @DisplayName("根据ID获取应收账详情 - 不存在，抛出异常")
    void testFindById_NotFound() {
        // Given
        when(contractPaymentStageRepository.findById(stageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            accountsReceivableService.findById(stageId);
        });
        verify(contractPaymentStageRepository, times(1)).findById(stageId);
    }

    @Test
    @DisplayName("更新应收账信息 - 成功")
    void testUpdate_Success() {
        // Given
        when(contractPaymentStageRepository.findById(stageId)).thenReturn(Optional.of(paymentStage));
        when(contractPaymentStageRepository.save(any(ContractPaymentStage.class))).thenReturn(paymentStage);

        // When
        AccountsReceivableResponse result = accountsReceivableService.update(stageId, updateRequest);

        // Then
        assertNotNull(result);
        verify(contractPaymentStageRepository, times(1)).findById(stageId);
        verify(contractPaymentStageRepository, times(1)).save(any(ContractPaymentStage.class));
    }

    @Test
    @DisplayName("更新应收账信息 - 应收账不存在，抛出异常")
    void testUpdate_NotFound() {
        // Given
        when(contractPaymentStageRepository.findById(stageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            accountsReceivableService.update(stageId, updateRequest);
        });
        verify(contractPaymentStageRepository, times(1)).findById(stageId);
        verify(contractPaymentStageRepository, never()).save(any(ContractPaymentStage.class));
    }

    @Test
    @DisplayName("查询未付清应收账 - 成功")
    void testFindUnpaid_Success() {
        // Given
        ContractPaymentStage unpaidStage = new ContractPaymentStage();
        unpaidStage.setId(UUID.randomUUID());
        unpaidStage.setContract(contract);
        unpaidStage.setAmountPayable(100000.0);
        unpaidStage.setAmountPaid(50000.0); // 未付清

        ContractPaymentStage paidStage = new ContractPaymentStage();
        paidStage.setId(UUID.randomUUID());
        paidStage.setContract(contract);
        paidStage.setAmountPayable(50000.0);
        paidStage.setAmountPaid(50000.0); // 已付清

        List<ContractPaymentStage> allStages = Arrays.asList(unpaidStage, paidStage);
        when(contractPaymentStageRepository.findAll()).thenReturn(allStages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.findUnpaid();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // 只返回未付清的
        assertEquals(50000.0, result.get(0).getUnpaidAmount());
        verify(contractPaymentStageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询未付清应收账 - 全部已付清")
    void testFindUnpaid_AllPaid() {
        // Given
        ContractPaymentStage paidStage = new ContractPaymentStage();
        paidStage.setId(UUID.randomUUID());
        paidStage.setContract(contract);
        paidStage.setAmountPayable(50000.0);
        paidStage.setAmountPaid(50000.0); // 已付清

        List<ContractPaymentStage> allStages = Arrays.asList(paidStage);
        when(contractPaymentStageRepository.findAll()).thenReturn(allStages);

        // When
        List<AccountsReceivableResponse> result = accountsReceivableService.findUnpaid();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size()); // 没有未付清的
        verify(contractPaymentStageRepository, times(1)).findAll();
    }
}

