package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BiddingRepository extends JpaRepository<Bidding, UUID> {

    /**
     * 根据投标编号查询
     */
    Optional<Bidding> findByBiddingNo(String biddingNo);

    /**
     * 根据客户ID查询投标记录
     */
    List<Bidding> findByCustomerIdOrderByBiddingDateDesc(UUID customerId);

    /**
     * 根据日期范围查询投标记录
     */
    @Query("SELECT b FROM Bidding b WHERE b.biddingDate BETWEEN :startDate AND :endDate ORDER BY b.biddingDate DESC")
    List<Bidding> findByBiddingDateBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

    /**
     * 搜索投标记录（支持按投标编号、项目名称、客户名称模糊查询）
     */
    @Query("SELECT b FROM Bidding b JOIN b.customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "b.biddingNo LIKE %:keyword% OR " +
           "b.projectName LIKE %:keyword% OR " +
           "c.customerName LIKE %:keyword%)")
    List<Bidding> searchBiddings(@Param("keyword") String keyword);
}

