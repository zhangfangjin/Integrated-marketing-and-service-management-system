package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.AnalysisModelPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 分析模型点位关联数据访问接口
 */
@Repository
public interface AnalysisModelPointRepository extends JpaRepository<AnalysisModelPoint, UUID> {

    /**
     * 根据分析模型ID查找点位关联列表
     */
    List<AnalysisModelPoint> findByAnalysisModelIdOrderBySortOrder(UUID analysisModelId);

    /**
     * 根据分析模型ID和曲线分组查找
     */
    List<AnalysisModelPoint> findByAnalysisModelIdAndCurveGroupOrderBySortOrder(UUID analysisModelId, String curveGroup);

    /**
     * 根据分析模型ID删除所有点位关联
     */
    void deleteByAnalysisModelId(UUID analysisModelId);

    /**
     * 根据点位ID查找
     */
    List<AnalysisModelPoint> findByDataPointId(UUID dataPointId);
}

















