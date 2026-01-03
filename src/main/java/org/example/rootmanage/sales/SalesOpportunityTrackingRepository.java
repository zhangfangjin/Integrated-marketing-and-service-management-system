package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesOpportunityTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalesOpportunityTrackingRepository extends JpaRepository<SalesOpportunityTracking, UUID> {

    /**
     * 根据销售机会ID查询跟踪记录
     */
    List<SalesOpportunityTracking> findByOpportunityIdOrderByTrackingTimeDesc(UUID opportunityId);
}

