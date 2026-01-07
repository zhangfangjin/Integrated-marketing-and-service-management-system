package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.FormulaParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 公式参数数据访问接口
 */
@Repository
public interface FormulaParameterRepository extends JpaRepository<FormulaParameter, UUID> {

    /**
     * 根据公式ID查找参数列表
     */
    List<FormulaParameter> findByFormulaIdOrderBySortOrder(UUID formulaId);

    /**
     * 根据公式ID删除所有参数
     */
    void deleteByFormulaId(UUID formulaId);

    /**
     * 根据点位ID查找
     */
    List<FormulaParameter> findByDataPointId(UUID dataPointId);
}

















