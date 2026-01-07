package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.SpaceNodeRequest;
import org.example.rootmanage.remotemonitoring.entity.SpaceNode;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;
import org.example.rootmanage.remotemonitoring.repository.SpaceNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 空间模型服务类
 * 提供空间节点（销售对象公司、设备安装区域等）的管理功能
 */
@Service
@RequiredArgsConstructor
public class SpaceNodeService {

    private final SpaceNodeRepository spaceNodeRepository;

    /**
     * 获取所有空间节点
     */
    @Transactional(readOnly = true)
    public List<SpaceNode> findAll() {
        return spaceNodeRepository.findAll();
    }

    /**
     * 获取所有根节点（构建树形结构）
     */
    @Transactional(readOnly = true)
    public List<SpaceNode> findRootNodes() {
        return spaceNodeRepository.findByParentIdIsNullOrderBySortOrder();
    }

    /**
     * 根据父节点ID获取子节点
     */
    @Transactional(readOnly = true)
    public List<SpaceNode> findByParentId(UUID parentId) {
        return spaceNodeRepository.findByParentIdOrderBySortOrder(parentId);
    }

    /**
     * 根据节点类型查找
     */
    @Transactional(readOnly = true)
    public List<SpaceNode> findByNodeType(SpaceNodeType nodeType) {
        return spaceNodeRepository.findByNodeTypeOrderBySortOrder(nodeType);
    }

    /**
     * 搜索空间节点
     */
    @Transactional(readOnly = true)
    public List<SpaceNode> searchNodes(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return spaceNodeRepository.searchNodes(keyword.trim());
        }
        return spaceNodeRepository.findAll();
    }

    /**
     * 根据ID查找空间节点
     */
    @Transactional(readOnly = true)
    public SpaceNode findById(UUID id) {
        return spaceNodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("空间节点不存在"));
    }

    /**
     * 创建空间节点
     */
    @Transactional
    public SpaceNode create(SpaceNodeRequest request) {
        // 检查编码是否已存在
        spaceNodeRepository.findByNodeCode(request.getNodeCode())
                .ifPresent(n -> {
                    throw new IllegalStateException("节点编码已存在");
                });

        SpaceNode node = new SpaceNode();
        node.setNodeCode(request.getNodeCode());
        node.setNodeName(request.getNodeName());
        node.setNodeType(request.getNodeType() != null ? request.getNodeType() : SpaceNodeType.OTHER);
        node.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        node.setContactPerson(request.getContactPerson());
        node.setContactPhone(request.getContactPhone());
        node.setAddress(request.getAddress());
        node.setLongitude(request.getLongitude());
        node.setLatitude(request.getLatitude());
        node.setDescription(request.getDescription());
        node.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        node.setRemark(request.getRemark());

        // 设置父节点和层级
        if (request.getParentId() != null) {
            SpaceNode parent = spaceNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父节点不存在"));
            node.setParentId(request.getParentId());
            node.setLevel(parent.getLevel() + 1);
        } else {
            node.setLevel(1);
        }

        return spaceNodeRepository.save(node);
    }

    /**
     * 更新空间节点
     */
    @Transactional
    public SpaceNode update(UUID id, SpaceNodeRequest request) {
        SpaceNode node = spaceNodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("空间节点不存在"));

        // 检查编码是否被其他节点使用
        if (!node.getNodeCode().equals(request.getNodeCode())) {
            spaceNodeRepository.findByNodeCode(request.getNodeCode())
                    .ifPresent(n -> {
                        throw new IllegalStateException("节点编码已被其他节点使用");
                    });
        }

        node.setNodeCode(request.getNodeCode());
        node.setNodeName(request.getNodeName());
        if (request.getNodeType() != null) {
            node.setNodeType(request.getNodeType());
        }
        if (request.getSortOrder() != null) {
            node.setSortOrder(request.getSortOrder());
        }
        node.setContactPerson(request.getContactPerson());
        node.setContactPhone(request.getContactPhone());
        node.setAddress(request.getAddress());
        node.setLongitude(request.getLongitude());
        node.setLatitude(request.getLatitude());
        node.setDescription(request.getDescription());
        if (request.getEnabled() != null) {
            node.setEnabled(request.getEnabled());
        }
        node.setRemark(request.getRemark());

        // 更新父节点
        if (request.getParentId() != null && !request.getParentId().equals(node.getParentId())) {
            // 防止循环引用
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("不能将节点设置为自己的父节点");
            }
            SpaceNode parent = spaceNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父节点不存在"));
            node.setParentId(request.getParentId());
            node.setLevel(parent.getLevel() + 1);
        } else if (request.getParentId() == null && node.getParentId() != null) {
            node.setParentId(null);
            node.setLevel(1);
        }

        return spaceNodeRepository.save(node);
    }

    /**
     * 删除空间节点
     */
    @Transactional
    public void delete(UUID id) {
        SpaceNode node = spaceNodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("空间节点不存在"));

        // 检查是否有子节点
        List<SpaceNode> children = spaceNodeRepository.findByParentIdOrderBySortOrder(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException("该节点存在子节点，无法删除");
        }

        spaceNodeRepository.delete(node);
    }
}

















