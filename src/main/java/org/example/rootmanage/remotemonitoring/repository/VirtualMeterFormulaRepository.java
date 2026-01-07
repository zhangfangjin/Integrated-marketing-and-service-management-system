package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.VirtualMeterFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 虚拟表计公式数据访问接口
 */
@Repository
public interface VirtualMeterFormulaRepository extends JpaRepository<VirtualMeterFormula, UUID> {

    /**
     * 根据公式编码查找
     */
    Optional<VirtualMeterFormula> findByFormulaCode(String formulaCode);

    /**
     * 根据输出点位ID查找
     */
    Optional<VirtualMeterFormula> findByOutputPointId(UUID outputPointId);

    /**
     * 查找启用的公式
     */
    List<VirtualMeterFormula> findByEnabled(Boolean enabled);

    /**
     * 搜索公式
     */
    @Query("SELECT v FROM VirtualMeterFormula v WHERE " +
            "v.formulaName LIKE %:keyword% OR " +
            "v.formulaCode LIKE %:keyword%")
    List<VirtualMeterFormula> searchFormulas(@Param("keyword") String keyword);
}

















