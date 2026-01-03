package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesOpportunityAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SalesOpportunityAssignmentRepository extends JpaRepository<SalesOpportunityAssignment, UUID> {

    /**
     * 根据销售机会ID查询分配记录
     */
    List<SalesOpportunityAssignment> findByOpportunityId(UUID opportunityId);

    /**
     * 根据片区ID查询分配记录
     */
    List<SalesOpportunityAssignment> findByAreaId(UUID areaId);

    /**
     * 根据营销人员ID查询分配记录
     */
    List<SalesOpportunityAssignment> findByPersonnelId(UUID personnelId);

    /**
     * 查询待分配的机会（没有分配记录的机会）
     */
    @Query("SELECT so FROM SalesOpportunity so WHERE so.submitted = true " +
           "AND NOT EXISTS (SELECT 1 FROM SalesOpportunityAssignment soa WHERE soa.opportunity.id = so.id)")
    List<org.example.rootmanage.sales.entity.SalesOpportunity> findUnassignedOpportunities();

    /**
     * 删除机会的所有分配记录
     */
    void deleteByOpportunityId(UUID opportunityId);
}

