package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.dto.CustomerRequest;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
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
 * 基本信息服务 - 客户管理功能测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("基本信息服务 - 客户管理测试")
class BasicInfoServiceCustomerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerKeyPersonRepository customerKeyPersonRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @Mock
    private org.example.rootmanage.account.UserAccountRepository userAccountRepository;

    @InjectMocks
    private BasicInfoService basicInfoService;

    private UUID customerId;
    private Customer customer;
    private CustomerRequest customerRequest;
    private OptionItem customerType;
    private OptionItem industry;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        // 创建测试客户实体
        customer = new Customer();
        customer.setId(customerId);
        customer.setCustomerName("测试客户公司");
        customer.setContactPerson("张三");
        customer.setContactPhone("13800138000");
        customer.setContactEmail("zhangsan@example.com");
        customer.setAddress("北京市朝阳区");
        customer.setCreditCode("91110000123456789X");
        customer.setLegalRepresentative("李四");
        customer.setRemark("测试备注");
        customer.setActive(true);

        // 创建测试请求DTO
        customerRequest = new CustomerRequest();
        customerRequest.setCustomerName("测试客户公司");
        customerRequest.setContactPerson("张三");
        customerRequest.setContactPhone("13800138000");
        customerRequest.setContactEmail("zhangsan@example.com");
        customerRequest.setAddress("北京市朝阳区");
        customerRequest.setCreditCode("91110000123456789X");
        customerRequest.setLegalRepresentative("李四");
        customerRequest.setRemark("测试备注");
        customerRequest.setActive(true);

        // 创建测试选项项
        customerType = new OptionItem();
        customerType.setId(UUID.randomUUID());
        customerType.setTitle("企业客户");

        industry = new OptionItem();
        industry.setId(UUID.randomUUID());
        industry.setTitle("制造业");
    }

    @Test
    @DisplayName("查询所有客户 - 成功")
    void testFindAllCustomers_Success() {
        // 准备数据
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        // 执行测试
        List<Customer> result = basicInfoService.findAllCustomers(null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试客户公司", result.get(0).getCustomerName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询客户 - 带关键词搜索")
    void testFindAllCustomers_WithKeyword() {
        // 准备数据
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.searchCustomers("测试")).thenReturn(customers);

        // 执行测试
        List<Customer> result = basicInfoService.findAllCustomers("测试");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试客户公司", result.get(0).getCustomerName());
        verify(customerRepository, times(1)).searchCustomers("测试");
    }

    @Test
    @DisplayName("创建客户 - 成功")
    void testCreateCustomer_Success() {
        // 准备数据
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // 执行测试
        Customer result = basicInfoService.createCustomer(customerRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("测试客户公司", result.getCustomerName());
        assertEquals("张三", result.getContactPerson());
        assertEquals("13800138000", result.getContactPhone());
        assertEquals(true, result.getActive());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("创建客户 - 带客户类型和行业")
    void testCreateCustomer_WithTypeAndIndustry() {
        // 准备数据
        UUID customerTypeId = customerType.getId();
        UUID industryId = industry.getId();
        customerRequest.setCustomerTypeId(customerTypeId);
        customerRequest.setIndustryId(industryId);

        when(optionItemRepository.findById(customerTypeId)).thenReturn(Optional.of(customerType));
        when(optionItemRepository.findById(industryId)).thenReturn(Optional.of(industry));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // 执行测试
        Customer result = basicInfoService.createCustomer(customerRequest);

        // 验证结果
        assertNotNull(result);
        verify(optionItemRepository, times(1)).findById(customerTypeId);
        verify(optionItemRepository, times(1)).findById(industryId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("创建客户 - 客户类型不存在")
    void testCreateCustomer_CustomerTypeNotFound() {
        // 准备数据
        UUID invalidTypeId = UUID.randomUUID();
        customerRequest.setCustomerTypeId(invalidTypeId);

        when(optionItemRepository.findById(invalidTypeId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.createCustomer(customerRequest)
        );

        assertEquals("客户类型选项不存在", exception.getMessage());
        verify(optionItemRepository, times(1)).findById(invalidTypeId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("创建客户 - 行业不存在")
    void testCreateCustomer_IndustryNotFound() {
        // 准备数据
        UUID invalidIndustryId = UUID.randomUUID();
        customerRequest.setIndustryId(invalidIndustryId);

        when(optionItemRepository.findById(invalidIndustryId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.createCustomer(customerRequest)
        );

        assertEquals("所属行业选项不存在", exception.getMessage());
        verify(optionItemRepository, times(1)).findById(invalidIndustryId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("更新客户 - 成功")
    void testUpdateCustomer_Success() {
        // 准备数据
        customerRequest.setCustomerName("更新后的客户名称");
        customer.setCustomerName("更新后的客户名称");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // 执行测试
        Customer result = basicInfoService.updateCustomer(customerId, customerRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("更新后的客户名称", result.getCustomerName());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("更新客户 - 客户不存在")
    void testUpdateCustomer_NotFound() {
        // 准备数据
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.updateCustomer(customerId, customerRequest)
        );

        assertEquals("客户不存在", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("更新客户 - 更新客户类型和行业")
    void testUpdateCustomer_UpdateTypeAndIndustry() {
        // 准备数据
        UUID newCustomerTypeId = UUID.randomUUID();
        UUID newIndustryId = UUID.randomUUID();
        OptionItem newCustomerType = new OptionItem();
        newCustomerType.setId(newCustomerTypeId);
        OptionItem newIndustry = new OptionItem();
        newIndustry.setId(newIndustryId);

        customerRequest.setCustomerTypeId(newCustomerTypeId);
        customerRequest.setIndustryId(newIndustryId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(optionItemRepository.findById(newCustomerTypeId)).thenReturn(Optional.of(newCustomerType));
        when(optionItemRepository.findById(newIndustryId)).thenReturn(Optional.of(newIndustry));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // 执行测试
        Customer result = basicInfoService.updateCustomer(customerId, customerRequest);

        // 验证结果
        assertNotNull(result);
        verify(optionItemRepository, times(1)).findById(newCustomerTypeId);
        verify(optionItemRepository, times(1)).findById(newIndustryId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("删除客户 - 成功（软删除）")
    void testDeleteCustomer_Success() {
        // 准备数据
        customer.setActive(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            return saved;
        });

        // 执行测试
        assertDoesNotThrow(() -> basicInfoService.deleteCustomer(customerId));

        // 验证结果 - 软删除：将 active 设置为 false 并保存
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
        assertFalse(customer.getActive(), "删除后 active 应该为 false");
    }

    @Test
    @DisplayName("删除客户 - 客户不存在")
    void testDeleteCustomer_NotFound() {
        // 准备数据
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.deleteCustomer(customerId)
        );

        assertEquals("客户不存在", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("创建客户 - active默认为true")
    void testCreateCustomer_ActiveDefaultTrue() {
        // 准备数据 - 不设置active
        customerRequest.setActive(null);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(customerId);
            return saved;
        });

        // 执行测试
        Customer result = basicInfoService.createCustomer(customerRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getActive());
    }

    @Test
    @DisplayName("更新客户 - active为null时不更新")
    void testUpdateCustomer_ActiveNullNotUpdate() {
        // 准备数据
        customer.setActive(false);
        customerRequest.setActive(null);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // 执行测试
        Customer result = basicInfoService.updateCustomer(customerId, customerRequest);

        // 验证结果 - active应该保持原值false
        assertFalse(result.getActive());
    }
}

