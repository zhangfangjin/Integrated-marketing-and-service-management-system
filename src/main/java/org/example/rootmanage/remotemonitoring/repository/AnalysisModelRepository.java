package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.AnalysisModel;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 分析模型数据访问接口
 */
@Repository
public interface AnalysisModelRepository extends JpaRepository<AnalysisModel, UUID> {

    /**
     * 根据模型编码查找
     */
    Optional<AnalysisModel> findByModelCode(String modelCode);

    /**
     * 根据模型类型查找
     */
    List<AnalysisModel> findByModelTypeOrderBySortOrder(AnalysisModelType modelType);

    /**
     * 根据父模型ID查找子模型
     */
    List<AnalysisModel> findByParentIdOrderBySortOrder(UUID parentId);

    /**
     * 查找所有根模型（无父模型）
     */
    List<AnalysisModel> findByParentIdIsNullOrderBySortOrder();

    /**
     * 查找启用的模型
     */
    List<AnalysisModel> findByEnabledOrderBySortOrder(Boolean enabled);

    /**
     * 搜索分析模型
     */
    @Query("SELECT a FROM AnalysisModel a WHERE " +
            "a.modelName LIKE %:keyword% OR " +
            "a.modelCode LIKE %:keyword%")
    List<AnalysisModel> searchAnalysisModels(@Param("keyword") String keyword);
}





