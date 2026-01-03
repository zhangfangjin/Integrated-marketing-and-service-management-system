package org.example.rootmanage.sales;

import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.basicinfo.CustomerRepository;
import org.example.rootmanage.basicinfo.MarketingPersonnelRepository;
import org.example.rootmanage.basicinfo.ProductRepository;
import org.example.rootmanage.basicinfo.SalesAreaRepository;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.basicinfo.entity.MarketingPersonnel;
import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.example.rootmanage.option.OptionItemRepository;
import org.example.rootmanage.sales.dto.*;
import org.example.rootmanage.sales.entity.SalesOpportunity;
import org.example.rootmanage.sales.entity.SalesOpportunityAssignment;
import org.example.rootmanage.sales.entity.SalesOpportunityTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 销售机会服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("销售机会服务测试")
class SalesServiceOpportunityTest {

    @Mock
    private SalesOpportunityRepository salesOpportunityRepository;

    @Mock
    private SalesOpportunityTrackingRepository salesOpportunityTrackingRepository;

    @Mock
    private SalesOpportunityAssignmentRepository salesOpportunityAssignmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @Mock
    private SalesAreaRepository salesAreaRepository;

    @Mock
    private MarketingPersonnelRepository marketingPersonnelRepository;

    @InjectMocks
    private SalesService salesService;

    private UUID opportunityId;
    private UUID customerId;
    private SalesOpportunity opportunity;
    private Customer customer;
    private SalesOpportunityRequest request;

    @BeforeEach
    void setUp() {
        opportunityId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setCustomerName("测试客户");

        opportunity = new SalesOpportunity();
        opportunity.setId(opportunityId);
        opportunity.setCustomer(customer);
        opportunity.setOpportunityName("测试机会");
        opportunity.setOpportunitySubject("测试主题");
        opportunity.setOpportunityDate(LocalDate.now());
        opportunity.setBudget(new BigDecimal("100000"));
        opportunity.setReceived(false);
        opportunity.setSubmitted(false);

        request = new SalesOpportunityRequest();
        request.setCustomerId(customerId);
        request.setOpportunityName("测试机会");
        request.setOpportunitySubject("测试主题");
        request.setOpportunityDate(LocalDate.now());
        request.setBudget(new BigDecimal("100000"));
    }

    @Test
    @DisplayName("创建销售机会 - 成功")
    void testCreateSalesOpportunity_Success() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(salesOpportunityRepository.save(any(SalesOpportunity.class))).thenReturn(opportunity);

        SalesOpportunity created = salesService.createSalesOpportunity(request);

        assertNotNull(created);
        assertEquals(opportunity.getOpportunityName(), created.getOpportunityName());
        verify(customerRepository, times(1)).findById(customerId);
        verify(salesOpportunityRepository, times(1)).save(any(SalesOpportunity.class));
    }

    @Test
    @DisplayName("创建销售机会 - 客户不存在")
    void testCreateSalesOpportunity_CustomerNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            salesService.createSalesOpportunity(request);
        });

        assertEquals("客户不存在", exception.getMessage());
        verify(salesOpportunityRepository, never()).save(any(SalesOpportunity.class));
    }

    @Test
    @DisplayName("更新销售机会 - 成功")
    void testUpdateSalesOpportunity_Success() {
        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(salesOpportunityRepository.save(any(SalesOpportunity.class))).thenReturn(opportunity);

        request.setOpportunityName("更新后的机会");
        SalesOpportunity updated = salesService.updateSalesOpportunity(opportunityId, request);

        assertNotNull(updated);
        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(salesOpportunityRepository, times(1)).save(any(SalesOpportunity.class));
    }

    @Test
    @DisplayName("提交销售机会 - 成功")
    void testSubmitSalesOpportunity_Success() {
        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        opportunity.setSubmitted(true);
        when(salesOpportunityRepository.save(any(SalesOpportunity.class))).thenReturn(opportunity);

        SalesOpportunity submitted = salesService.submitSalesOpportunity(opportunityId);

        assertNotNull(submitted);
        assertTrue(submitted.getSubmitted());
        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(salesOpportunityRepository, times(1)).save(any(SalesOpportunity.class));
    }

    @Test
    @DisplayName("传递销售机会 - 成功")
    void testTransferSalesOpportunity_Success() {
        UUID areaId1 = UUID.randomUUID();
        UUID areaId2 = UUID.randomUUID();
        SalesArea area1 = new SalesArea();
        area1.setId(areaId1);
        SalesArea area2 = new SalesArea();
        area2.setId(areaId2);

        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(salesAreaRepository.findById(areaId1)).thenReturn(Optional.of(area1));
        when(salesAreaRepository.findById(areaId2)).thenReturn(Optional.of(area2));
        when(salesOpportunityAssignmentRepository.findByOpportunityId(opportunityId))
                .thenReturn(Arrays.asList());

        OpportunityTransferRequest transferRequest = new OpportunityTransferRequest();
        transferRequest.setOpportunityId(opportunityId);
        transferRequest.setTargetAreaIds(Arrays.asList(areaId1, areaId2));

        assertDoesNotThrow(() -> salesService.transferSalesOpportunity(transferRequest));

        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(salesAreaRepository, times(1)).findById(areaId1);
        verify(salesAreaRepository, times(1)).findById(areaId2);
        verify(salesOpportunityAssignmentRepository, times(2)).save(any(SalesOpportunityAssignment.class));
    }

    @Test
    @DisplayName("分配员工 - 成功")
    void testAssignSalesOpportunity_Success() {
        UUID personnelId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();
        MarketingPersonnel personnel = new MarketingPersonnel();
        personnel.setId(personnelId);
        SalesArea area = new SalesArea();
        area.setId(areaId);
        personnel.setResponsibleArea(area);

        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(marketingPersonnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));
        when(salesOpportunityAssignmentRepository.findByOpportunityId(opportunityId))
                .thenReturn(Arrays.asList());

        OpportunityAssignRequest assignRequest = new OpportunityAssignRequest();
        assignRequest.setOpportunityId(opportunityId);
        assignRequest.setPersonnelIds(Arrays.asList(personnelId));

        assertDoesNotThrow(() -> salesService.assignSalesOpportunity(assignRequest));

        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(marketingPersonnelRepository, times(1)).findById(personnelId);
        verify(salesOpportunityAssignmentRepository, times(1)).save(any(SalesOpportunityAssignment.class));
    }

    @Test
    @DisplayName("关闭机会 - 成功")
    void testCloseSalesOpportunity_Success() {
        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        opportunity.setCloseReason("客户取消项目");
        when(salesOpportunityRepository.save(any(SalesOpportunity.class))).thenReturn(opportunity);

        OpportunityCloseRequest closeRequest = new OpportunityCloseRequest();
        closeRequest.setOpportunityId(opportunityId);
        closeRequest.setCloseReason("客户取消项目");

        SalesOpportunity closed = salesService.closeSalesOpportunity(closeRequest);

        assertNotNull(closed);
        assertEquals("客户取消项目", closed.getCloseReason());
        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(salesOpportunityRepository, times(1)).save(any(SalesOpportunity.class));
    }

    @Test
    @DisplayName("创建跟踪记录 - 成功")
    void testCreateTracking_Success() {
        UUID trackerId = UUID.randomUUID();
        UserAccount tracker = new UserAccount();
        tracker.setId(trackerId);

        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(userAccountRepository.findById(trackerId)).thenReturn(Optional.of(tracker));

        SalesOpportunityTrackingRequest trackingRequest = new SalesOpportunityTrackingRequest();
        trackingRequest.setOpportunityId(opportunityId);
        trackingRequest.setTrackerId(trackerId);
        trackingRequest.setTrackingTime(LocalDateTime.now());
        trackingRequest.setMatter("客户拜访");
        trackingRequest.setDescription("讨论项目需求");

        SalesOpportunityTracking tracking = new SalesOpportunityTracking();
        tracking.setId(UUID.randomUUID());
        tracking.setOpportunity(opportunity);
        tracking.setTracker(tracker);
        when(salesOpportunityTrackingRepository.save(any(SalesOpportunityTracking.class))).thenReturn(tracking);

        SalesOpportunityTracking created = salesService.createTracking(trackingRequest);

        assertNotNull(created);
        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
        verify(salesOpportunityTrackingRepository, times(1)).save(any(SalesOpportunityTracking.class));
    }

    @Test
    @DisplayName("查询跟踪记录 - 成功")
    void testFindTrackingByOpportunityId_Success() {
        SalesOpportunityTracking tracking = new SalesOpportunityTracking();
        tracking.setId(UUID.randomUUID());
        tracking.setOpportunity(opportunity);

        when(salesOpportunityTrackingRepository.findByOpportunityIdOrderByTrackingTimeDesc(opportunityId))
                .thenReturn(Arrays.asList(tracking));

        List<SalesOpportunityTracking> trackings = salesService.findTrackingByOpportunityId(opportunityId);

        assertNotNull(trackings);
        assertEquals(1, trackings.size());
        verify(salesOpportunityTrackingRepository, times(1))
                .findByOpportunityIdOrderByTrackingTimeDesc(opportunityId);
    }

    @Test
    @DisplayName("删除销售机会 - 成功（同时删除关联记录）")
    void testDeleteSalesOpportunity_Success() {
        SalesOpportunityTracking tracking = new SalesOpportunityTracking();
        tracking.setId(UUID.randomUUID());

        when(salesOpportunityTrackingRepository.findByOpportunityIdOrderByTrackingTimeDesc(opportunityId))
                .thenReturn(Arrays.asList(tracking));
        doNothing().when(salesOpportunityAssignmentRepository).deleteByOpportunityId(opportunityId);
        doNothing().when(salesOpportunityTrackingRepository).delete(any(SalesOpportunityTracking.class));
        doNothing().when(salesOpportunityRepository).deleteById(opportunityId);

        assertDoesNotThrow(() -> salesService.deleteSalesOpportunity(opportunityId));

        verify(salesOpportunityAssignmentRepository, times(1)).deleteByOpportunityId(opportunityId);
        verify(salesOpportunityTrackingRepository, times(1)).findByOpportunityIdOrderByTrackingTimeDesc(opportunityId);
        verify(salesOpportunityTrackingRepository, times(1)).delete(any(SalesOpportunityTracking.class));
        verify(salesOpportunityRepository, times(1)).deleteById(opportunityId);
    }
}

