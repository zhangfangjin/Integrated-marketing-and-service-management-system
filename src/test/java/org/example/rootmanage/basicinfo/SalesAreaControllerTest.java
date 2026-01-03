package org.example.rootmanage.basicinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.basicinfo.dto.SalesAreaRequest;
import org.example.rootmanage.basicinfo.entity.SalesArea;
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
 * 销售片区管理Controller测试类
 */
@WebMvcTest(
        controllers = BasicInfoController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("销售片区管理Controller测试")
class SalesAreaControllerTest {

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

    private UUID salesAreaId;
    private SalesArea salesArea;
    private SalesAreaRequest salesAreaRequest;

    @BeforeEach
    void setUp() {
        salesAreaId = UUID.randomUUID();

        // 创建测试销售片区实体
        salesArea = new SalesArea();
        salesArea.setId(salesAreaId);
        salesArea.setAreaName("上海片区");
        salesArea.setAreaCode("001");
        salesArea.setRemark("上海地区销售片区");
        salesArea.setActive(true);

        // 创建测试请求DTO
        salesAreaRequest = new SalesAreaRequest();
        salesAreaRequest.setAreaName("上海片区");
        salesAreaRequest.setAreaCode("001");
        salesAreaRequest.setRemark("上海地区销售片区");
        salesAreaRequest.setActive(true);
    }

    @Test
    @DisplayName("查询销售片区列表 - 成功")
    void testGetSalesAreas_Success() throws Exception {
        // 准备数据
        List<SalesArea> salesAreas = Arrays.asList(salesArea);
        when(basicInfoService.findAllSalesAreas(null)).thenReturn(salesAreas);

        // 执行请求
        mockMvc.perform(get("/api/basicinfo/sales-areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(salesAreaId.toString()))
                .andExpect(jsonPath("$[0].areaName").value("上海片区"))
                .andExpect(jsonPath("$[0].areaCode").value("001"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).findAllSalesAreas(null);
    }

    @Test
    @DisplayName("查询销售片区列表 - 带关键词搜索")
    void testGetSalesAreas_WithKeyword() throws Exception {
        // 准备数据
        List<SalesArea> salesAreas = Arrays.asList(salesArea);
        when(basicInfoService.findAllSalesAreas("上海")).thenReturn(salesAreas);

        // 执行请求
        mockMvc.perform(get("/api/basicinfo/sales-areas")
                        .param("keyword", "上海"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].areaName").value("上海片区"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).findAllSalesAreas("上海");
    }

    @Test
    @DisplayName("创建销售片区 - 成功")
    void testCreateSalesArea_Success() throws Exception {
        // 准备数据
        when(basicInfoService.createSalesArea(any(SalesAreaRequest.class))).thenReturn(salesArea);

        // 执行请求
        mockMvc.perform(post("/api/basicinfo/sales-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salesAreaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salesAreaId.toString()))
                .andExpect(jsonPath("$.areaName").value("上海片区"))
                .andExpect(jsonPath("$.areaCode").value("001"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).createSalesArea(any(SalesAreaRequest.class));
    }

    @Test
    @DisplayName("创建销售片区 - 验证失败（片区名称为空）")
    void testCreateSalesArea_ValidationFailed() throws Exception {
        // 准备数据 - 片区名称为空
        SalesAreaRequest invalidRequest = new SalesAreaRequest();
        invalidRequest.setAreaCode("001");

        // 执行请求
        mockMvc.perform(post("/api/basicinfo/sales-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法未被调用
        verify(basicInfoService, never()).createSalesArea(any(SalesAreaRequest.class));
    }

    @Test
    @DisplayName("更新销售片区 - 成功")
    void testUpdateSalesArea_Success() throws Exception {
        // 准备数据
        salesAreaRequest.setAreaName("更新后的片区名称");
        salesArea.setAreaName("更新后的片区名称");
        when(basicInfoService.updateSalesArea(eq(salesAreaId), any(SalesAreaRequest.class))).thenReturn(salesArea);

        // 执行请求
        mockMvc.perform(put("/api/basicinfo/sales-areas/{id}", salesAreaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salesAreaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salesAreaId.toString()))
                .andExpect(jsonPath("$.areaName").value("更新后的片区名称"));

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).updateSalesArea(eq(salesAreaId), any(SalesAreaRequest.class));
    }

    @Test
    @DisplayName("更新销售片区 - 片区不存在")
    void testUpdateSalesArea_NotFound() throws Exception {
        // 准备数据
        when(basicInfoService.updateSalesArea(eq(salesAreaId), any(SalesAreaRequest.class)))
                .thenThrow(new IllegalArgumentException("销售片区不存在"));

        // 执行请求
        mockMvc.perform(put("/api/basicinfo/sales-areas/{id}", salesAreaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salesAreaRequest)))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).updateSalesArea(eq(salesAreaId), any(SalesAreaRequest.class));
    }

    @Test
    @DisplayName("删除销售片区 - 成功")
    void testDeleteSalesArea_Success() throws Exception {
        // 准备数据
        doNothing().when(basicInfoService).deleteSalesArea(salesAreaId);

        // 执行请求
        mockMvc.perform(delete("/api/basicinfo/sales-areas/{id}", salesAreaId))
                .andExpect(status().isOk());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).deleteSalesArea(salesAreaId);
    }

    @Test
    @DisplayName("删除销售片区 - 片区不存在")
    void testDeleteSalesArea_NotFound() throws Exception {
        // 准备数据
        doThrow(new IllegalArgumentException("销售片区不存在"))
                .when(basicInfoService).deleteSalesArea(salesAreaId);

        // 执行请求
        mockMvc.perform(delete("/api/basicinfo/sales-areas/{id}", salesAreaId))
                .andExpect(status().isBadRequest());

        // 验证服务方法被调用
        verify(basicInfoService, times(1)).deleteSalesArea(salesAreaId);
    }
}

