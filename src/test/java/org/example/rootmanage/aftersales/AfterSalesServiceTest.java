package org.example.rootmanage.aftersales;

import org.example.rootmanage.aftersales.dto.*;
import org.example.rootmanage.aftersales.entity.AfterSalesActivity;
import org.example.rootmanage.aftersales.entity.AfterSalesOrder;
import org.example.rootmanage.aftersales.entity.AfterSalesType;
import org.example.rootmanage.aftersales.repository.AfterSalesActivityRepository;
import org.example.rootmanage.aftersales.repository.AfterSalesOrderRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 售后服务服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("售后服务服务测试")
class AfterSalesServiceTest {

    @Mock
    private AfterSalesOrderRepository afterSalesOrderRepository;

    @Mock
    private AfterSalesActivityRepository afterSalesActivityRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private AfterSalesService afterSalesService;

    private UUID orderId;
    private UUID contractId;
    private AfterSalesOrder order;
    private Contract contract;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        contractId = UUID.randomUUID();

        contract = new Contract();
        contract.setId(contractId);
        contract.setContractNumber("CT001");
        contract.setContractName("测试合同");

        order = new AfterSalesOrder();
        order.setId(orderId);
        order.setServiceOrderNumber("AS20240101001");
        order.setServiceType(AfterSalesType.TROUBLESHOOTING);
        order.setContractNumber("CT001");
        order.setContractName("测试合同");
        order.setCustomerUnit("测试客户");
        order.setServiceStatus("待分配");
    }

    @Test
    @DisplayName("创建售后服务单 - 成功")
    void testCreate_Success() {
        // Given
        AfterSalesOrderRequest request = new AfterSalesOrderRequest();
        request.setServiceType(AfterSalesType.TROUBLESHOOTING);
        request.setContractId(contractId);
        request.setCustomerUnit("测试客户");
        request.setServiceDate(Date.valueOf(LocalDate.now()));

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(afterSalesOrderRepository.findByServiceOrderNumber(anyString())).thenReturn(Optional.empty());
        when(afterSalesOrderRepository.save(any(AfterSalesOrder.class))).thenAnswer(invocation -> {
            AfterSalesOrder o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setServiceOrderNumber("AS20240101001");
            return o;
        });

        // When
        AfterSalesOrderResponse result = afterSalesService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(AfterSalesType.TROUBLESHOOTING, result.getServiceType());
        assertEquals("CT001", result.getContractNumber());
        assertEquals("测试客户", result.getCustomerUnit());
        verify(contractRepository, times(1)).findById(contractId);
        verify(afterSalesOrderRepository, times(1)).save(any(AfterSalesOrder.class));
    }

    @Test
    @DisplayName("创建售后服务单 - 合同不存在，抛出异常")
    void testCreate_ContractNotExists() {
        // Given
        AfterSalesOrderRequest request = new AfterSalesOrderRequest();
        request.setContractId(contractId);

        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            afterSalesService.create(request);
        });
        verify(contractRepository, times(1)).findById(contractId);
        verify(afterSalesOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("指派受理人员 - 成功")
    void testAssignHandler_Success() {
        // Given
        AssignHandlerRequest request = new AssignHandlerRequest();
        UUID handlerId = UUID.randomUUID();
        request.setHandlerId(handlerId);
        request.setHandlerName("测试处理人");

        when(afterSalesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(afterSalesOrderRepository.save(any(AfterSalesOrder.class))).thenReturn(order);

        // When
        AfterSalesOrderResponse result = afterSalesService.assignHandler(orderId, request);

        // Then
        assertNotNull(result);
        assertEquals(handlerId, result.getHandlerId());
        assertEquals("测试处理人", result.getHandlerName());
        assertEquals("处理中", result.getServiceStatus());
        verify(afterSalesOrderRepository, times(1)).findById(orderId);
        verify(afterSalesOrderRepository, times(1)).save(any(AfterSalesOrder.class));
    }

    @Test
    @DisplayName("完成售后服务 - 成功")
    void testCompleteService_Success() {
        // Given
        CompleteServiceRequest request = new CompleteServiceRequest();
        request.setDeviceNumber("DEV001");
        request.setDeviceName("测试设备");
        request.setServiceSummary("服务完成");

        when(afterSalesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(afterSalesOrderRepository.save(any(AfterSalesOrder.class))).thenReturn(order);

        // When
        AfterSalesOrderResponse result = afterSalesService.completeService(orderId, request);

        // Then
        assertNotNull(result);
        assertEquals("DEV001", result.getDeviceNumber());
        assertEquals("测试设备", result.getDeviceName());
        assertEquals("服务完成", result.getServiceSummary());
        assertEquals("已完成", result.getServiceStatus());
        assertNotNull(result.getCompletionDate());
        verify(afterSalesOrderRepository, times(1)).findById(orderId);
        verify(afterSalesOrderRepository, times(1)).save(any(AfterSalesOrder.class));
    }

    @Test
    @DisplayName("评价售后服务 - 成功")
    void testEvaluateService_Success() {
        // Given
        EvaluateServiceRequest request = new EvaluateServiceRequest();
        request.setEvaluation("优");
        request.setEvaluatorName("测试评价人");

        order.setServiceStatus("已完成");
        when(afterSalesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(afterSalesOrderRepository.save(any(AfterSalesOrder.class))).thenReturn(order);

        // When
        AfterSalesOrderResponse result = afterSalesService.evaluateService(orderId, request);

        // Then
        assertNotNull(result);
        assertEquals("优", result.getEvaluation());
        assertEquals("测试评价人", result.getEvaluatorName());
        assertNotNull(result.getEvaluationTime());
        assertTrue(result.getIsClosed());
        verify(afterSalesOrderRepository, times(1)).findById(orderId);
        verify(afterSalesOrderRepository, times(1)).save(any(AfterSalesOrder.class));
    }

    @Test
    @DisplayName("添加售后服务活动 - 成功")
    void testAddActivity_Success() {
        // Given
        AfterSalesActivityRequest request = new AfterSalesActivityRequest();
        request.setAfterSalesOrderId(orderId);
        request.setActivityType("跟踪");
        request.setDescription("进行中");
        request.setOperatorName("测试操作人");

        when(afterSalesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(afterSalesActivityRepository.save(any(AfterSalesActivity.class))).thenAnswer(invocation -> {
            AfterSalesActivity activity = invocation.getArgument(0);
            activity.setId(UUID.randomUUID());
            return activity;
        });

        // When
        AfterSalesActivity result = afterSalesService.addActivity(request);

        // Then
        assertNotNull(result);
        assertEquals("跟踪", result.getActivityType());
        assertEquals("进行中", result.getDescription());
        assertEquals("测试操作人", result.getOperatorName());
        assertEquals(orderId, result.getAfterSalesOrder().getId());
        verify(afterSalesOrderRepository, times(1)).findById(orderId);
        verify(afterSalesActivityRepository, times(1)).save(any(AfterSalesActivity.class));
    }

    @Test
    @DisplayName("根据ID查找售后服务单 - 成功")
    void testFindById_Success() {
        // Given
        when(afterSalesOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        AfterSalesOrderResponse result = afterSalesService.findById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("AS20240101001", result.getServiceOrderNumber());
        verify(afterSalesOrderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("搜索售后服务单 - 成功")
    void testSearchAfterSalesOrders_Success() {
        // Given
        String keyword = "测试";
        when(afterSalesOrderRepository.searchAfterSalesOrders(keyword))
                .thenReturn(Arrays.asList(order));

        // When
        List<AfterSalesOrderResponse> result = afterSalesService.searchAfterSalesOrders(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(afterSalesOrderRepository, times(1)).searchAfterSalesOrders(keyword);
    }
}


