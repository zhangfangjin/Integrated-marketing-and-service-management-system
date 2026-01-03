package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.CustomerVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CustomerVisitRepository extends JpaRepository<CustomerVisit, UUID> {

    /**
     * 根据客户ID查询来访记录
     */
    List<CustomerVisit> findByCustomerIdOrderByVisitTimeDesc(UUID customerId);

    /**
     * 根据时间范围查询来访记录
     */
    @Query("SELECT v FROM CustomerVisit v WHERE v.visitTime BETWEEN :startTime AND :endTime ORDER BY v.visitTime DESC")
    List<CustomerVisit> findByVisitTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
}

