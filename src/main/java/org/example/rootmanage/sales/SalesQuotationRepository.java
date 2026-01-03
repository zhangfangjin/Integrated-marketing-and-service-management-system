package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesQuotationRepository extends JpaRepository<SalesQuotation, UUID> {

    /**
     * 根据报价单号查询
     */
    Optional<SalesQuotation> findByQuotationNo(String quotationNo);

    /**
     * 根据客户ID查询报价单
     */
    List<SalesQuotation> findByCustomerIdOrderByQuotationDateDesc(UUID customerId);

    /**
     * 根据日期范围查询报价单
     */
    @Query("SELECT q FROM SalesQuotation q WHERE q.quotationDate BETWEEN :startDate AND :endDate ORDER BY q.quotationDate DESC")
    List<SalesQuotation> findByQuotationDateBetween(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 搜索报价单（支持按报价单号、客户名称模糊查询）
     */
    @Query("SELECT q FROM SalesQuotation q JOIN q.customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "q.quotationNo LIKE %:keyword% OR " +
           "c.customerName LIKE %:keyword%)")
    List<SalesQuotation> searchQuotations(@Param("keyword") String keyword);
}

