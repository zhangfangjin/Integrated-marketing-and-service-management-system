package org.example.rootmanage.aftersales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.aftersales.dto.AfterSalesExperienceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesExperience;
import org.example.rootmanage.auth.TokenService;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 售后经验控制器测试类
 */
@WebMvcTest(
        controllers = AfterSalesExperienceController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("售后经验控制器测试")
class AfterSalesExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AfterSalesExperienceService experienceService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID experienceId;
    private AfterSalesExperience experience;

    @BeforeEach
    void setUp() {
        experienceId = UUID.randomUUID();

        experience = new AfterSalesExperience();
        experience.setId(experienceId);
        experience.setCustomerName("测试客户");
        experience.setDeviceType("泵");
        experience.setCaseType("故障处理");
        experience.setExperienceSummary("测试经验总结");
    }

    @Test
    @DisplayName("获取售后经验列表 - 成功")
    void testList_Success() throws Exception {
        // Given
        List<AfterSalesExperience> experiences = Arrays.asList(experience);
        when(experienceService.findAll()).thenReturn(experiences);

        // When & Then
        mockMvc.perform(get("/api/after-sales-experiences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("测试客户"))
                .andExpect(jsonPath("$[0].deviceType").value("泵"));

        verify(experienceService, times(1)).findAll();
    }

    @Test
    @DisplayName("获取售后经验列表 - 带关键词搜索")
    void testList_WithKeyword() throws Exception {
        // Given
        List<AfterSalesExperience> experiences = Arrays.asList(experience);
        when(experienceService.searchAfterSalesExperiences("测试")).thenReturn(experiences);

        // When & Then
        mockMvc.perform(get("/api/after-sales-experiences")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("测试客户"));

        verify(experienceService, times(1)).searchAfterSalesExperiences("测试");
    }

    @Test
    @DisplayName("根据ID获取售后经验 - 成功")
    void testGetById_Success() throws Exception {
        // Given
        when(experienceService.findById(experienceId)).thenReturn(experience);

        // When & Then
        mockMvc.perform(get("/api/after-sales-experiences/{id}", experienceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("测试客户"))
                .andExpect(jsonPath("$.deviceType").value("泵"));

        verify(experienceService, times(1)).findById(experienceId);
    }

    @Test
    @DisplayName("创建售后经验 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        AfterSalesExperienceRequest request = new AfterSalesExperienceRequest();
        request.setCustomerName("新客户");
        request.setDeviceType("泵");
        request.setCaseType("故障处理");
        request.setCaseRegistrationDate(Date.valueOf(LocalDate.now()));
        request.setExperienceSummary("新的经验总结");

        AfterSalesExperience createdExperience = new AfterSalesExperience();
        createdExperience.setId(UUID.randomUUID());
        createdExperience.setCustomerName("新客户");

        when(experienceService.create(any(AfterSalesExperienceRequest.class)))
                .thenReturn(createdExperience);

        // When & Then
        mockMvc.perform(post("/api/after-sales-experiences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("新客户"));

        verify(experienceService, times(1))
                .create(any(AfterSalesExperienceRequest.class));
    }

    @Test
    @DisplayName("更新售后经验 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        AfterSalesExperienceRequest request = new AfterSalesExperienceRequest();
        request.setCustomerName("更新后的客户");
        request.setDeviceType("泵");
        request.setExperienceSummary("更新后的经验总结");

        experience.setCustomerName("更新后的客户");
        experience.setExperienceSummary("更新后的经验总结");

        when(experienceService.update(eq(experienceId), any(AfterSalesExperienceRequest.class)))
                .thenReturn(experience);

        // When & Then
        mockMvc.perform(put("/api/after-sales-experiences/{id}", experienceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("更新后的客户"));

        verify(experienceService, times(1))
                .update(eq(experienceId), any(AfterSalesExperienceRequest.class));
    }

    @Test
    @DisplayName("删除售后经验 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(experienceService).delete(experienceId);

        // When & Then
        mockMvc.perform(delete("/api/after-sales-experiences/{id}", experienceId))
                .andExpect(status().isOk());

        verify(experienceService, times(1)).delete(experienceId);
    }
}


