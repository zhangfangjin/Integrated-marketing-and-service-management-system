package org.example.rootmanage.companyinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.companyinfo.dto.CompanyInfoReleaseRequest;
import org.example.rootmanage.companyinfo.entity.CompanyInfoRelease;
import org.example.rootmanage.option.OptionItem;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 公司信息发布管理Controller测试类
 */
@WebMvcTest(
        controllers = CompanyInfoController.class,
        excludeAutoConfiguration = {},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("公司信息发布管理Controller测试")
class CompanyInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyInfoService companyInfoService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID releaseId;
    private UUID typeId;
    private CompanyInfoRelease release;
    private CompanyInfoReleaseRequest request;

    @BeforeEach
    void setUp() {
        releaseId = UUID.randomUUID();
        typeId = UUID.randomUUID();

        OptionItem type = new OptionItem();
        type.setId(typeId);
        type.setTitle("新产品资料");

        release = new CompanyInfoRelease();
        release.setId(releaseId);
        release.setTitle("测试标题");
        release.setType(type);
        release.setContent("测试内容");
        release.setAttachments("attachment1.pdf");
        release.setActive(true);

        request = new CompanyInfoReleaseRequest();
        request.setTitle("测试标题");
        request.setTypeId(typeId);
        request.setContent("测试内容");
        request.setAttachments("attachment1.pdf");
    }

    @Test
    @DisplayName("查询信息发布列表 - 成功")
    void testGetReleases_Success() throws Exception {
        List<CompanyInfoRelease> releases = Arrays.asList(release);
        when(companyInfoService.findAllReleases(null)).thenReturn(releases);

        mockMvc.perform(get("/api/company-info/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(releaseId.toString()))
                .andExpect(jsonPath("$[0].title").value("测试标题"));

        verify(companyInfoService, times(1)).findAllReleases(null);
    }

    @Test
    @DisplayName("查询信息发布列表 - 带关键词")
    void testGetReleases_WithKeyword() throws Exception {
        List<CompanyInfoRelease> releases = Arrays.asList(release);
        when(companyInfoService.findAllReleases("测试")).thenReturn(releases);

        mockMvc.perform(get("/api/company-info/releases")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(companyInfoService, times(1)).findAllReleases("测试");
    }

    @Test
    @DisplayName("根据ID查询信息发布 - 成功")
    void testGetReleaseById_Success() throws Exception {
        when(companyInfoService.findReleaseById(releaseId)).thenReturn(Optional.of(release));

        mockMvc.perform(get("/api/company-info/releases/{id}", releaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(releaseId.toString()))
                .andExpect(jsonPath("$.title").value("测试标题"));

        verify(companyInfoService, times(1)).findReleaseById(releaseId);
    }

    @Test
    @DisplayName("根据ID查询信息发布 - 不存在")
    void testGetReleaseById_NotFound() throws Exception {
        when(companyInfoService.findReleaseById(releaseId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/company-info/releases/{id}", releaseId))
                .andExpect(status().isNotFound());

        verify(companyInfoService, times(1)).findReleaseById(releaseId);
    }

    @Test
    @DisplayName("创建信息发布 - 成功")
    void testCreateRelease_Success() throws Exception {
        when(companyInfoService.createRelease(any(CompanyInfoReleaseRequest.class))).thenReturn(release);

        mockMvc.perform(post("/api/company-info/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(releaseId.toString()))
                .andExpect(jsonPath("$.title").value("测试标题"));

        verify(companyInfoService, times(1)).createRelease(any(CompanyInfoReleaseRequest.class));
    }

    @Test
    @DisplayName("创建信息发布 - 验证失败")
    void testCreateRelease_ValidationFailed() throws Exception {
        request.setTitle(""); // 标题为空，验证失败

        mockMvc.perform(post("/api/company-info/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(companyInfoService, never()).createRelease(any());
    }

    @Test
    @DisplayName("更新信息发布 - 成功")
    void testUpdateRelease_Success() throws Exception {
        when(companyInfoService.updateRelease(eq(releaseId), any(CompanyInfoReleaseRequest.class))).thenReturn(release);

        mockMvc.perform(put("/api/company-info/releases/{id}", releaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(releaseId.toString()))
                .andExpect(jsonPath("$.title").value("测试标题"));

        verify(companyInfoService, times(1)).updateRelease(eq(releaseId), any(CompanyInfoReleaseRequest.class));
    }

    @Test
    @DisplayName("删除信息发布 - 成功")
    void testDeleteRelease_Success() throws Exception {
        doNothing().when(companyInfoService).deleteRelease(releaseId);

        mockMvc.perform(delete("/api/company-info/releases/{id}", releaseId))
                .andExpect(status().isNoContent());

        verify(companyInfoService, times(1)).deleteRelease(releaseId);
    }
}

