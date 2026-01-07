package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.SpaceNodeRequest;
import org.example.rootmanage.remotemonitoring.entity.SpaceNode;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;
import org.example.rootmanage.remotemonitoring.repository.SpaceNodeRepository;
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
 * 空间模型服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("空间模型服务测试")
class SpaceNodeServiceTest {

    @Mock
    private SpaceNodeRepository spaceNodeRepository;

    @InjectMocks
    private SpaceNodeService spaceNodeService;

    private UUID nodeId;
    private UUID parentId;
    private SpaceNode parentNode;
    private SpaceNode childNode;

    @BeforeEach
    void setUp() {
        nodeId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        parentNode = new SpaceNode();
        parentNode.setId(parentId);
        parentNode.setNodeCode("COMPANY001");
        parentNode.setNodeName("测试公司");
        parentNode.setNodeType(SpaceNodeType.COMPANY);
        parentNode.setLevel(1);

        childNode = new SpaceNode();
        childNode.setId(nodeId);
        childNode.setNodeCode("REGION001");
        childNode.setNodeName("测试区域");
        childNode.setNodeType(SpaceNodeType.REGION);
        childNode.setParentId(parentId);
        childNode.setLevel(2);
    }

    @Test
    @DisplayName("创建空间节点 - 成功")
    void testCreate_Success() {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("NODE001");
        request.setNodeName("测试节点");
        request.setNodeType(SpaceNodeType.COMPANY);
        request.setSortOrder(1);
        request.setEnabled(true);

        when(spaceNodeRepository.findByNodeCode("NODE001")).thenReturn(Optional.empty());
        when(spaceNodeRepository.save(any(SpaceNode.class))).thenAnswer(invocation -> {
            SpaceNode node = invocation.getArgument(0);
            node.setId(UUID.randomUUID());
            return node;
        });

        // When
        SpaceNode result = spaceNodeService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("NODE001", result.getNodeCode());
        assertEquals("测试节点", result.getNodeName());
        assertEquals(SpaceNodeType.COMPANY, result.getNodeType());
        assertEquals(1, result.getLevel());
        assertTrue(result.getEnabled());
        verify(spaceNodeRepository, times(1)).findByNodeCode("NODE001");
        verify(spaceNodeRepository, times(1)).save(any(SpaceNode.class));
    }

    @Test
    @DisplayName("创建空间节点 - 带父节点")
    void testCreate_WithParent() {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("NODE002");
        request.setNodeName("子节点");
        request.setNodeType(SpaceNodeType.REGION);
        request.setParentId(parentId);

        when(spaceNodeRepository.findByNodeCode("NODE002")).thenReturn(Optional.empty());
        when(spaceNodeRepository.findById(parentId)).thenReturn(Optional.of(parentNode));
        when(spaceNodeRepository.save(any(SpaceNode.class))).thenAnswer(invocation -> {
            SpaceNode node = invocation.getArgument(0);
            node.setId(UUID.randomUUID());
            return node;
        });

        // When
        SpaceNode result = spaceNodeService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(parentId, result.getParentId());
        assertEquals(2, result.getLevel());
        verify(spaceNodeRepository, times(1)).findById(parentId);
    }

    @Test
    @DisplayName("创建空间节点 - 编码已存在，抛出异常")
    void testCreate_NodeCodeExists() {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("EXISTING");
        request.setNodeName("测试节点");

        when(spaceNodeRepository.findByNodeCode("EXISTING")).thenReturn(Optional.of(parentNode));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            spaceNodeService.create(request);
        });
        verify(spaceNodeRepository, times(1)).findByNodeCode("EXISTING");
        verify(spaceNodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新空间节点 - 成功")
    void testUpdate_Success() {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("UPDATED");
        request.setNodeName("更新后的节点");
        request.setNodeType(SpaceNodeType.BUILDING);
        request.setEnabled(false);

        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.of(childNode));
        when(spaceNodeRepository.findByNodeCode("UPDATED")).thenReturn(Optional.empty());
        when(spaceNodeRepository.save(any(SpaceNode.class))).thenReturn(childNode);

        // When
        SpaceNode result = spaceNodeService.update(nodeId, request);

        // Then
        assertNotNull(result);
        assertEquals("UPDATED", result.getNodeCode());
        assertEquals("更新后的节点", result.getNodeName());
        assertEquals(SpaceNodeType.BUILDING, result.getNodeType());
        assertFalse(result.getEnabled());
        verify(spaceNodeRepository, times(1)).findById(nodeId);
        verify(spaceNodeRepository, times(1)).save(any(SpaceNode.class));
    }

    @Test
    @DisplayName("更新空间节点 - 节点不存在，抛出异常")
    void testUpdate_NodeNotExists() {
        // Given
        SpaceNodeRequest request = new SpaceNodeRequest();
        request.setNodeCode("UPDATED");

        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            spaceNodeService.update(nodeId, request);
        });
        verify(spaceNodeRepository, times(1)).findById(nodeId);
        verify(spaceNodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("删除空间节点 - 成功")
    void testDelete_Success() {
        // Given
        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.of(childNode));
        when(spaceNodeRepository.findByParentIdOrderBySortOrder(nodeId)).thenReturn(List.of());

        // When
        spaceNodeService.delete(nodeId);

        // Then
        verify(spaceNodeRepository, times(1)).findById(nodeId);
        verify(spaceNodeRepository, times(1)).findByParentIdOrderBySortOrder(nodeId);
        verify(spaceNodeRepository, times(1)).delete(childNode);
    }

    @Test
    @DisplayName("删除空间节点 - 存在子节点，抛出异常")
    void testDelete_HasChildren() {
        // Given
        SpaceNode grandChild = new SpaceNode();
        grandChild.setId(UUID.randomUUID());

        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.of(childNode));
        when(spaceNodeRepository.findByParentIdOrderBySortOrder(nodeId))
                .thenReturn(Arrays.asList(grandChild));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            spaceNodeService.delete(nodeId);
        });
        verify(spaceNodeRepository, times(1)).findById(nodeId);
        verify(spaceNodeRepository, times(1)).findByParentIdOrderBySortOrder(nodeId);
        verify(spaceNodeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("根据ID查找空间节点 - 成功")
    void testFindById_Success() {
        // Given
        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.of(childNode));

        // When
        SpaceNode result = spaceNodeService.findById(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(nodeId, result.getId());
        assertEquals("REGION001", result.getNodeCode());
        verify(spaceNodeRepository, times(1)).findById(nodeId);
    }

    @Test
    @DisplayName("根据ID查找空间节点 - 不存在，抛出异常")
    void testFindById_NotExists() {
        // Given
        when(spaceNodeRepository.findById(nodeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            spaceNodeService.findById(nodeId);
        });
        verify(spaceNodeRepository, times(1)).findById(nodeId);
    }

    @Test
    @DisplayName("搜索空间节点 - 成功")
    void testSearchNodes_Success() {
        // Given
        String keyword = "测试";
        when(spaceNodeRepository.searchNodes(keyword)).thenReturn(Arrays.asList(parentNode, childNode));

        // When
        List<SpaceNode> result = spaceNodeService.searchNodes(keyword);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(spaceNodeRepository, times(1)).searchNodes(keyword);
    }

    @Test
    @DisplayName("获取根节点 - 成功")
    void testFindRootNodes_Success() {
        // Given
        when(spaceNodeRepository.findByParentIdIsNullOrderBySortOrder())
                .thenReturn(Arrays.asList(parentNode));

        // When
        List<SpaceNode> result = spaceNodeService.findRootNodes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(parentId, result.get(0).getId());
        verify(spaceNodeRepository, times(1)).findByParentIdIsNullOrderBySortOrder();
    }
}

















