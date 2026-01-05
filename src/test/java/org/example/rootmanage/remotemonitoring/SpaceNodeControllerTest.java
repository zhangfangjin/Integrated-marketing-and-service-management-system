package org.example.rootmanage.remotemonitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rootmanage.auth.TokenService;
import org.example.rootmanage.config.RestExceptionHandler;
import org.example.rootmanage.config.WebConfig;
import org.example.rootmanage.module.ModuleRepository;
import org.example.rootmanage.permission.PermissionService;
import org.example.rootmanage.remotemonitoring.dto.SpaceNodeRequest;
import org.example.rootmanage.remotemonitoring.entity.SpaceNode;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;
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
 * 空间模型控制器测试类
 */
@WebMvcTest(
        controllers = SpaceNodeController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
@DisplayName("空间模型控制器测试")
class SpaceNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpaceNodeService spaceNodeService;

    // Mock拦截器需要的依赖，避免Bean创建失败
    @MockBean
    private TokenService tokenService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private ModuleRepository moduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID nodeId;
    private SpaceNode spaceNode;

    @BeforeEach
    void setUp() {
        nodeId = UUID.randomUUID();

        spaceNode = new SpaceNode();
        spaceNode.setId(nodeId);
        spaceNode.setNodeCode("NODE001");
        spaceNode.setNodeName("测试节点");
        spaceNode.setNodeType(SpaceNodeType.COMPANY);
        spaceNode.setLevel(1);
        spaceNode.setEnabled(true);
    }

    @Test
    @DisplayName("获取所有空间节点 - 成功")
    void testFindAll_Success() throws Exception {
        // Given
        List<SpaceNode> nodes = Arrays.asList(spaceNode);
        when(spaceNodeService.findAll()).thenReturn(nodes);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/space-nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeCode").value("NODE001"))
                .andExpect(jsonPath("$[0].nodeName").value("测试节点"));

        verify(spaceNodeService, times(1)).findAll();
    }

    @Test
    @DisplayName("获取所有空间节点 - 带关键词搜索")
    void testFindAll_WithKeyword() throws Exception {
        // Given
        List<SpaceNode> nodes = Arrays.asList(spaceNode);
        when(spaceNodeService.searchNodes("测试")).thenReturn(nodes);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/space-nodes")
                        .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeName").value("测试节点"));

        verify(spaceNodeService, times(1)).searchNodes("测试");
    }

    @Test
    @DisplayName("获取空间节点树形结构 - 成功")
    void testGetTree_Success() throws Exception {
        // Given
        List<SpaceNode> rootNodes = Arrays.asList(spaceNode);
        when(spaceNodeService.findRootNodes()).thenReturn(rootNodes);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/space-nodes/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeCode").value("NODE001"));

        verify(spaceNodeService, times(1)).findRootNodes();
    }

    @Test
    @DisplayName("根据ID获取空间节点 - 成功")
    void testFindById_Success() throws Exception {
        // Given
        when(spaceNodeService.findById(nodeId)).thenReturn(spaceNode);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/space-nodes/{id}", nodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeCode").value("NODE001"))
                .andExpect(jsonPath("$.nodeName").value("测试节点"));

        verify(spaceNodeService, times(1)).findById(nodeId);
    }

    @Test
    @DisplayName("创建空间节点 - 成功")
    void testCreate_Success() throws Exception {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("NODE002");
        request.setNodeName("新节点");
        request.setNodeType(SpaceNodeType.REGION);
        request.setEnabled(true);

        SpaceNode createdNode = new SpaceNode();
        createdNode.setId(UUID.randomUUID());
        createdNode.setNodeCode("NODE002");
        createdNode.setNodeName("新节点");

        when(spaceNodeService.create(any(SpaceNodeRequest.class))).thenReturn(createdNode);

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/space-nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeCode").value("NODE002"))
                .andExpect(jsonPath("$.nodeName").value("新节点"));

        verify(spaceNodeService, times(1)).create(any(SpaceNodeRequest.class));
    }

    @Test
    @DisplayName("创建空间节点 - 验证失败")
    void testCreate_ValidationFailed() throws Exception {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        // 缺少必填字段 nodeCode 和 nodeName

        // When & Then
        mockMvc.perform(post("/api/remote-monitoring/space-nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(spaceNodeService, never()).create(any());
    }

    @Test
    @DisplayName("更新空间节点 - 成功")
    void testUpdate_Success() throws Exception {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("NODE001");
        request.setNodeName("更新后的节点");
        request.setNodeType(SpaceNodeType.COMPANY);
        request.setEnabled(false);

        spaceNode.setNodeName("更新后的节点");
        spaceNode.setEnabled(false);

        when(spaceNodeService.update(eq(nodeId), any(SpaceNodeRequest.class))).thenReturn(spaceNode);

        // When & Then
        mockMvc.perform(put("/api/remote-monitoring/space-nodes/{id}", nodeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeName").value("更新后的节点"));

        verify(spaceNodeService, times(1)).update(eq(nodeId), any(SpaceNodeRequest.class));
    }

    @Test
    @DisplayName("删除空间节点 - 成功")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(spaceNodeService).delete(nodeId);

        // When & Then
        mockMvc.perform(delete("/api/remote-monitoring/space-nodes/{id}", nodeId))
                .andExpect(status().isNoContent());

        verify(spaceNodeService, times(1)).delete(nodeId);
    }

    @Test
    @DisplayName("根据节点类型查找 - 成功")
    void testFindByNodeType_Success() throws Exception {
        // Given
        List<SpaceNode> nodes = Arrays.asList(spaceNode);
        when(spaceNodeService.findByNodeType(SpaceNodeType.COMPANY)).thenReturn(nodes);

        // When & Then
        mockMvc.perform(get("/api/remote-monitoring/space-nodes/type/COMPANY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nodeType").value("COMPANY"));

        verify(spaceNodeService, times(1)).findByNodeType(SpaceNodeType.COMPANY);
    }
}


