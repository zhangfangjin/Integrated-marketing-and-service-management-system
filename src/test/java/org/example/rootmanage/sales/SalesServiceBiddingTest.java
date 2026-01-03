package org.example.rootmanage.sales;

import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.basicinfo.CustomerRepository;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.option.OptionItemRepository;
import org.example.rootmanage.sales.dto.BiddingRequest;
import org.example.rootmanage.sales.entity.Bidding;
import org.example.rootmanage.sales.entity.SalesOpportunity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 投标管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("投标管理服务测试")
class SalesServiceBiddingTest {

    @Mock
    private BiddingRepository biddingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @Mock
    private SalesOpportunityRepository salesOpportunityRepository;

    @InjectMocks
    private SalesService salesService;

    private UUID biddingId;
    private UUID customerId;
    private UUID opportunityId;
    private Bidding bidding;
    private Customer customer;
    private SalesOpportunity opportunity;
    private BiddingRequest request;

    @BeforeEach
    void setUp() {
        biddingId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        opportunityId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setCustomerName("测试客户");

        opportunity = new SalesOpportunity();
        opportunity.setId(opportunityId);
        opportunity.setOpportunityName("测试机会");

        bidding = new Bidding();
        bidding.setId(biddingId);
        bidding.setBiddingNo("BID001");
        bidding.setBiddingName("测试投标");
        bidding.setCustomer(customer);
        bidding.setProjectName("测试项目");
        bidding.setBiddingDate(LocalDate.now());
        bidding.setBiddingAmount(new BigDecimal("500000"));

        request = new BiddingRequest();
        request.setBiddingNo("BID001");
        request.setBiddingName("测试投标");
        request.setCustomerId(customerId);
        request.setProjectName("测试项目");
        request.setBiddingDate(LocalDate.now());
        request.setBiddingAmount(new BigDecimal("500000"));
    }

    @Test
    @DisplayName("查询所有投标 - 无关键词")
    void testFindAllBiddings_NoKeyword() {
        List<Bidding> expectedBiddings = Arrays.asList(bidding);
        when(biddingRepository.findAll()).thenReturn(expectedBiddings);

        List<Bidding> actualBiddings = salesService.findAllBiddings(null);

        assertNotNull(actualBiddings);
        assertEquals(1, actualBiddings.size());
        assertEquals(bidding.getBiddingNo(), actualBiddings.get(0).getBiddingNo());
        verify(biddingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("创建投标 - 成功")
    void testCreateBidding_Success() {
        when(biddingRepository.findByBiddingNo(request.getBiddingNo())).thenReturn(Optional.empty());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(biddingRepository.save(any(Bidding.class))).thenReturn(bidding);

        Bidding created = salesService.createBidding(request);

        assertNotNull(created);
        assertEquals(bidding.getBiddingNo(), created.getBiddingNo());
        verify(customerRepository, times(1)).findById(customerId);
        verify(biddingRepository, times(1)).save(any(Bidding.class));
    }

    @Test
    @DisplayName("创建投标 - 投标编号已存在")
    void testCreateBidding_BiddingNoExists() {
        when(biddingRepository.findByBiddingNo(request.getBiddingNo())).thenReturn(Optional.of(bidding));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            salesService.createBidding(request);
        });

        assertEquals("投标编号已存在", exception.getMessage());
        verify(biddingRepository, never()).save(any(Bidding.class));
    }

    @Test
    @DisplayName("创建投标 - 关联销售机会")
    void testCreateBidding_WithOpportunity() {
        request.setOpportunityId(opportunityId);
        request.setOpportunityName("测试机会");
        request.setOpportunityNo("OPP001");

        when(biddingRepository.findByBiddingNo(request.getBiddingNo())).thenReturn(Optional.empty());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(salesOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(biddingRepository.save(any(Bidding.class))).thenReturn(bidding);

        Bidding created = salesService.createBidding(request);

        assertNotNull(created);
        verify(salesOpportunityRepository, times(1)).findById(opportunityId);
    }

    @Test
    @DisplayName("更新投标 - 成功")
    void testUpdateBidding_Success() {
        when(biddingRepository.findById(biddingId)).thenReturn(Optional.of(bidding));
        when(biddingRepository.findByBiddingNo(request.getBiddingNo())).thenReturn(Optional.of(bidding));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(biddingRepository.save(any(Bidding.class))).thenReturn(bidding);

        request.setBiddingName("更新后的投标");
        Bidding updated = salesService.updateBidding(biddingId, request);

        assertNotNull(updated);
        verify(biddingRepository, times(1)).findById(biddingId);
        verify(biddingRepository, times(1)).save(any(Bidding.class));
    }

    @Test
    @DisplayName("更新投标 - 投标编号被其他投标使用")
    void testUpdateBidding_BiddingNoUsedByOther() {
        Bidding otherBidding = new Bidding();
        otherBidding.setId(UUID.randomUUID());
        otherBidding.setBiddingNo("BID001");

        when(biddingRepository.findById(biddingId)).thenReturn(Optional.of(bidding));
        when(biddingRepository.findByBiddingNo(request.getBiddingNo())).thenReturn(Optional.of(otherBidding));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            salesService.updateBidding(biddingId, request);
        });

        assertEquals("投标编号已被其他投标记录使用", exception.getMessage());
    }

    @Test
    @DisplayName("删除投标 - 成功")
    void testDeleteBidding_Success() {
        doNothing().when(biddingRepository).deleteById(biddingId);

        assertDoesNotThrow(() -> salesService.deleteBidding(biddingId));

        verify(biddingRepository, times(1)).deleteById(biddingId);
    }
}

