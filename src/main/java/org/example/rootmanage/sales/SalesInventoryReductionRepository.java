package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesInventoryReduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesInventoryReductionRepository extends JpaRepository<SalesInventoryReduction, UUID> {

    /**
     * 根据降库单号查询
     */
    Optional<SalesInventoryReduction> findByReductionNo(String reductionNo);

    /**
     * 根据产品ID查询降库记录
     */
    List<SalesInventoryReduction> findByProductIdOrderByReductionDateDesc(UUID productId);

    /**
     * 根据日期范围查询降库记录
     */
    @Query("SELECT r FROM SalesInventoryReduction r WHERE r.reductionDate BETWEEN :startDate AND :endDate ORDER BY r.reductionDate DESC")
    List<SalesInventoryReduction> findByReductionDateBetween(@Param("startDate") LocalDate startDate, 
                                                               @Param("endDate") LocalDate endDate);

    /**
     * 搜索降库记录（支持按降库单号、产品名称模糊查询）
     */
    @Query("SELECT r FROM SalesInventoryReduction r JOIN r.product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "r.reductionNo LIKE %:keyword% OR " +
           "p.productName LIKE %:keyword%)")
    List<SalesInventoryReduction> searchReductions(@Param("keyword") String keyword);
}

