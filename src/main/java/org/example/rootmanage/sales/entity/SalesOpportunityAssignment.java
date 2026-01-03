package org.example.rootmanage.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.basicinfo.entity.MarketingPersonnel;
import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.example.rootmanage.common.BaseEntity;

/**
 * 销售机会分配实体（记录机会分配给哪些片区和员工）
 */
@Getter
@Setter
@Entity
@Table(name = "sales_opportunity_assignment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesOpportunityAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private SalesOpportunity opportunity; // 销售机会

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private SalesArea area; // 分配的片区

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private MarketingPersonnel personnel; // 分配的营销人员

    @Column(nullable = false)
    private Boolean isPrimary = false; // 是否主要负责片区
}

