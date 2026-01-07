package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.SpaceNode;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 空间节点数据访问接口
 */
@Repository
public interface SpaceNodeRepository extends JpaRepository<SpaceNode, UUID> {

    /**
     * 根据节点编码查找
     */
    Optional<SpaceNode> findByNodeCode(String nodeCode);

    /**
     * 根据父节点ID查找子节点
     */
    List<SpaceNode> findByParentIdOrderBySortOrder(UUID parentId);

    /**
     * 查找所有根节点（无父节点）
     */
    List<SpaceNode> findByParentIdIsNullOrderBySortOrder();

    /**
     * 根据节点类型查找
     */
    List<SpaceNode> findByNodeTypeOrderBySortOrder(SpaceNodeType nodeType);

    /**
     * 根据节点层级查找
     */
    List<SpaceNode> findByLevelOrderBySortOrder(Integer level);

    /**
     * 搜索节点
     */
    @Query("SELECT s FROM SpaceNode s WHERE " +
            "s.nodeName LIKE %:keyword% OR " +
            "s.nodeCode LIKE %:keyword% OR " +
            "s.description LIKE %:keyword%")
    List<SpaceNode> searchNodes(@Param("keyword") String keyword);

    /**
     * 根据启用状态查找
     */
    List<SpaceNode> findByEnabledOrderBySortOrder(Boolean enabled);
}

















