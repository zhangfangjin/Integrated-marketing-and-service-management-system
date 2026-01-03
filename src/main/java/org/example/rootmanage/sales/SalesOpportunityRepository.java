package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SalesOpportunityRepository extends JpaRepository<SalesOpportunity, UUID> {

    /**
     * 根据客户ID查询销售机会
     */
    List<SalesOpportunity> findByCustomerIdOrderByCreateTimeDesc(UUID customerId);

    /**
     * 根据销售负责人ID查询销售机会
     */
    List<SalesOpportunity> findBySalespersonIdOrderByCreateTimeDesc(UUID salespersonId);

    /**
     * 搜索销售机会（支持按机会名称、客户名称模糊查询）
     */
    @Query("SELECT o FROM SalesOpportunity o JOIN o.customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "o.opportunityName LIKE %:keyword% OR " +
           "c.customerName LIKE %:keyword%)")
    List<SalesOpportunity> searchOpportunities(@Param("keyword") String keyword);
}

