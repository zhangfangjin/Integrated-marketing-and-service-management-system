package org.example.rootmanage.basicinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.basicinfo.dto.CustomerRequest;
import org.example.rootmanage.basicinfo.entity.Customer;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 客户管理Controller测试类
 */
@WebMvcTest(
        controllers = BasicInfoController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("客户管理Controller测试")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasicInfoService basicInfoService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID customerId;
    private Customer customer;
    private CustomerRequest customerRequest;

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
    }

    @Test
    @DisplayName("查询客户列表 - 成功")
    void testGetCustomers_Success() throws Exception {
        // 准备数据
        List<Customer> customers = Arrays.asList(customer);
        when(basicInfoService.findAllCustomers(null)).thenReturn(customers);

        // 执行请求
        mockMvc.perform(get("/api/basicinfo/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(customerId.toString()))
                .andExpect(jsonPath("$[0].customerName").value("测试客户公司"))
                .andExpect(jsonPath("$[0].contactPerson").value("张三"))
                .andExpect(jsonPath("$[0].contactPhone").value("13800138000"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).findAllCustomers(null);
    }

    @Test
    @DisplayName("查询客户列表 - 带关键词搜索")
    void testGetCustomers_WithKeyword() throws Exception {
        // 准备数据
        List<Customer> customers = Arrays.asList(customer);
        when(basicInfoService.findAllCustomers("测试")).thenReturn(customers);

        // 执行请求
        mockMvc.perform(get("/api/basicinfo/customers")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerName").value("测试客户公司"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).findAllCustomers("测试");
    }

    @Test
    @DisplayName("创建客户 - 成功")
    void testCreateCustomer_Success() throws Exception {
        // 准备数据
        when(basicInfoService.createCustomer(any(CustomerRequest.class))).thenReturn(customer);

        // 执行请求
        mockMvc.perform(post("/api/basicinfo/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.customerName").value("测试客户公司"))
                .andExpect(jsonPath("$.contactPerson").value("张三"))
                .andExpect(jsonPath("$.contactPhone").value("13800138000"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("创建客户 - 验证失败（客户名称为空）")
    void testCreateCustomer_ValidationFailed() throws Exception {
        // 准备数据 - 客户名称为空
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setContactPerson("张三");
        invalidRequest.setContactPhone("13800138000");

        // 执行请求
        mockMvc.perform(post("/api/basicinfo/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法未被调用
        verify(basicInfoService, never()).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("更新客户 - 成功")
    void testUpdateCustomer_Success() throws Exception {
        // 准备数据
        customerRequest.setCustomerName("更新后的客户名称");
        customer.setCustomerName("更新后的客户名称");
        when(basicInfoService.updateCustomer(eq(customerId), any(CustomerRequest.class))).thenReturn(customer);

        // 执行请求
        mockMvc.perform(put("/api/basicinfo/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.customerName").value("更新后的客户名称"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).updateCustomer(eq(customerId), any(CustomerRequest.class));
    }

    @Test
    @DisplayName("更新客户 - 客户不存在")
    void testUpdateCustomer_NotFound() throws Exception {
        // 准备数据
        when(basicInfoService.updateCustomer(eq(customerId), any(CustomerRequest.class)))
                .thenThrow(new IllegalArgumentException("客户不存在"));

        // 执行请求
        mockMvc.perform(put("/api/basicinfo/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).updateCustomer(eq(customerId), any(CustomerRequest.class));
    }

    @Test
    @DisplayName("删除客户 - 成功")
    void testDeleteCustomer_Success() throws Exception {
        // 准备数据
        doNothing().when(basicInfoService).deleteCustomer(customerId);

        // 执行请求
        mockMvc.perform(delete("/api/basicinfo/customers/{id}", customerId))
                .andExpect(status().isOk());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).deleteCustomer(customerId);
    }

    @Test
    @DisplayName("删除客户 - 客户不存在")
    void testDeleteCustomer_NotFound() throws Exception {
        // 准备数据
        doThrow(new IllegalArgumentException("客户不存在"))
                .when(basicInfoService).deleteCustomer(customerId);

        // 执行请求
        mockMvc.perform(delete("/api/basicinfo/customers/{id}", customerId))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).deleteCustomer(customerId);
    }
}

