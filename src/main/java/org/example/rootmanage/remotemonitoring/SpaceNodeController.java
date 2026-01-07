package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.SpaceNodeRequest;
import org.example.rootmanage.remotemonitoring.entity.SpaceNode;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 空间模型控制器
 * 提供空间节点（销售对象公司、设备安装区域等）的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/space-nodes")
@RequiredArgsConstructor
public class SpaceNodeController {

    private final SpaceNodeService spaceNodeService;

    /**
     * 获取所有空间节点
     */
    @GetMapping
    public List<SpaceNode> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return spaceNodeService.searchNodes(keyword);
        }
        return spaceNodeService.findAll();
    }

    /**
     * 获取空间节点树形结构（根节点）
     */
    @GetMapping("/tree")
    public List<SpaceNode> getTree() {
        return spaceNodeService.findRootNodes();
    }

    /**
     * 根据父节点ID获取子节点
     */
    @GetMapping("/parent/{parentId}")
    public List<SpaceNode> findByParentId(@PathVariable UUID parentId) {
        return spaceNodeService.findByParentId(parentId);
    }

    /**
     * 根据节点类型查找
     */
    @GetMapping("/type/{nodeType}")
    public List<SpaceNode> findByNodeType(@PathVariable SpaceNodeType nodeType) {
        return spaceNodeService.findByNodeType(nodeType);
    }

    /**
     * 根据ID获取空间节点
     */
    @GetMapping("/{id}")
    public SpaceNode findById(@PathVariable UUID id) {
        return spaceNodeService.findById(id);
    }

    /**
     * 创建空间节点
     */
    @PostMapping
    public SpaceNode create(@Valid @RequestBody SpaceNodeRequest request) {
        return spaceNodeService.create(request);
    }

    /**
     * 更新空间节点
     */
    @PutMapping("/{id}")
    public SpaceNode update(@PathVariable UUID id, @Valid @RequestBody SpaceNodeRequest request) {
        return spaceNodeService.update(id, request);
    }

    /**
     * 删除空间节点
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        spaceNodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

















