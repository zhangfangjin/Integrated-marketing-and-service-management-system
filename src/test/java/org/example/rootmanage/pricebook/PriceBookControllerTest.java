package org.example.rootmanage.pricebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.pricebook.dto.PriceBookRequest;
import org.example.rootmanage.pricebook.entity.PriceBook;
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
 * 价格本管理Controller测试类
 */
@WebMvcTest(
        controllers = PriceBookController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("价格本管理Controller测试")
class PriceBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceBookService priceBookService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID priceBookId;
    private UUID productId;
    private PriceBook priceBook;
    private Product product;
    private PriceBookRequest priceBookRequest;

    @BeforeEach
    void setUp() {
        priceBookId = UUID.randomUUID();
        productId = UUID.randomUUID();

        // 创建测试产品实体
        product = new Product();
        product.setId(productId);
        product.setProductCode("P001");
        product.setProductName("测试产品");
        product.setActive(true);

        // 创建测试价格本实体
        priceBook = new PriceBook();
        priceBook.setId(priceBookId);
        priceBook.setProduct(product);
        priceBook.setVersionNumber("V1.0");
        priceBook.setPriceType("标准价格");
        priceBook.setUnitPrice(new BigDecimal("100.00"));
        priceBook.setCurrency("CNY");
        priceBook.setEffectiveDate(Date.valueOf("2024-01-01"));
        priceBook.setExpiryDate(Date.valueOf("2024-12-31"));
        priceBook.setActive(true);
        priceBook.setRemark("测试备注");

        // 创建测试请求DTO
        priceBookRequest = new PriceBookRequest();
        priceBookRequest.setProductId(productId);
        priceBookRequest.setVersionNumber("V1.0");
        priceBookRequest.setPriceType("标准价格");
        priceBookRequest.setUnitPrice(new BigDecimal("100.00"));
        priceBookRequest.setCurrency("CNY");
        priceBookRequest.setEffectiveDate(Date.valueOf("2024-01-01"));
        priceBookRequest.setExpiryDate(Date.valueOf("2024-12-31"));
        priceBookRequest.setActive(true);
        priceBookRequest.setRemark("测试备注");
    }

    @Test
    @DisplayName("查询价格本列表 - 成功")
    void testGetPriceBooks_Success() throws Exception {
        // 准备数据
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookService.findAllActive()).thenReturn(priceBooks);

        // 执行请求
        mockMvc.perform(get("/api/pricebook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(priceBookId.toString()))
                .andExpect(jsonPath("$[0].versionNumber").value("V1.0"))
                .andExpect(jsonPath("$[0].priceType").value("标准价格"))
                .andExpect(jsonPath("$[0].unitPrice").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("CNY"));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("查询价格本列表 - 带关键词搜索")
    void testGetPriceBooks_WithKeyword() throws Exception {
        // 准备数据
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookService.searchByKeyword("测试")).thenReturn(priceBooks);

        // 执行请求
        mockMvc.perform(get("/api/pricebook")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].versionNumber").value("V1.0"));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).searchByKeyword("测试");
    }

    @Test
    @DisplayName("根据ID获取价格本 - 成功")
    void testGetPriceBookById_Success() throws Exception {
        // 准备数据
        when(priceBookService.findById(priceBookId)).thenReturn(priceBook);

        // 执行请求
        mockMvc.perform(get("/api/pricebook/{id}", priceBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(priceBookId.toString()))
                .andExpect(jsonPath("$.versionNumber").value("V1.0"))
                .andExpect(jsonPath("$.priceType").value("标准价格"))
                .andExpect(jsonPath("$.unitPrice").value(100.00));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).findById(priceBookId);
    }

    @Test
    @DisplayName("根据ID获取价格本 - 不存在")
    void testGetPriceBookById_NotFound() throws Exception {
        // 准备数据
        when(priceBookService.findById(priceBookId))
                .thenThrow(new IllegalArgumentException("价格记录不存在"));

        // 执行请求
        mockMvc.perform(get("/api/pricebook/{id}", priceBookId))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(priceBookService, times(1)).findById(priceBookId);
    }

    @Test
    @DisplayName("根据产品ID获取价格本列表 - 成功")
    void testGetPriceBooksByProductId_Success() throws Exception {
        // 准备数据
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookService.findByProductId(productId)).thenReturn(priceBooks);

        // 执行请求
        mockMvc.perform(get("/api/pricebook/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(priceBookId.toString()));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("创建价格本 - 成功")
    void testCreatePriceBook_Success() throws Exception {
        // 准备数据
        when(priceBookService.create(any(PriceBookRequest.class))).thenReturn(priceBook);

        // 执行请求
        mockMvc.perform(post("/api/pricebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceBookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(priceBookId.toString()))
                .andExpect(jsonPath("$.versionNumber").value("V1.0"))
                .andExpect(jsonPath("$.priceType").value("标准价格"))
                .andExpect(jsonPath("$.unitPrice").value(100.00));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).create(any(PriceBookRequest.class));
    }

    @Test
    @DisplayName("创建价格本 - 验证失败（产品ID为空）")
    void testCreatePriceBook_ValidationFailed() throws Exception {
        // 准备数据 - 产品ID为空
        PriceBookRequest invalidRequest = new PriceBookRequest();
        invalidRequest.setVersionNumber("V1.0");
        invalidRequest.setPriceType("标准价格");
        invalidRequest.setUnitPrice(new BigDecimal("100.00"));

        // 执行请求
        mockMvc.perform(post("/api/pricebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法未被调用
        verify(priceBookService, never()).create(any(PriceBookRequest.class));
    }

    @Test
    @DisplayName("更新价格本 - 成功")
    void testUpdatePriceBook_Success() throws Exception {
        // 准备数据
        priceBookRequest.setVersionNumber("V2.0");
        priceBook.setVersionNumber("V2.0");
        when(priceBookService.update(eq(priceBookId), any(PriceBookRequest.class))).thenReturn(priceBook);

        // 执行请求
        mockMvc.perform(put("/api/pricebook/{id}", priceBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceBookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(priceBookId.toString()))
                .andExpect(jsonPath("$.versionNumber").value("V2.0"));

        // 验证服务方法被调用
        verify(priceBookService, times(1)).update(eq(priceBookId), any(PriceBookRequest.class));
    }

    @Test
    @DisplayName("更新价格本 - 价格本不存在")
    void testUpdatePriceBook_NotFound() throws Exception {
        // 准备数据
        when(priceBookService.update(eq(priceBookId), any(PriceBookRequest.class)))
                .thenThrow(new IllegalArgumentException("价格记录不存在"));

        // 执行请求
        mockMvc.perform(put("/api/pricebook/{id}", priceBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceBookRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(priceBookService, times(1)).update(eq(priceBookId), any(PriceBookRequest.class));
    }

    @Test
    @DisplayName("删除价格本 - 成功")
    void testDeletePriceBook_Success() throws Exception {
        // 准备数据
        doNothing().when(priceBookService).delete(priceBookId);

        // 执行请求
        mockMvc.perform(delete("/api/pricebook/{id}", priceBookId))
                .andExpect(status().isOk());

        // 验证服务方法被调用
        verify(priceBookService, times(1)).delete(priceBookId);
    }

    @Test
    @DisplayName("删除价格本 - 价格本不存在")
    void testDeletePriceBook_NotFound() throws Exception {
        // 准备数据
        doThrow(new IllegalArgumentException("价格记录不存在"))
                .when(priceBookService).delete(priceBookId);

        // 执行请求
        mockMvc.perform(delete("/api/pricebook/{id}", priceBookId))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(priceBookService, times(1)).delete(priceBookId);
    }
}

