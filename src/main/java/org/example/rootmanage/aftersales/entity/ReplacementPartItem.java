package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

/**
 * 换件清单项实体类
 * 用于存储维护计划中的换件清单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "replacement_part_item")
public class ReplacementPartItem extends BaseEntity {

    /**
     * 关联的维护计划
     */
    @ManyToOne
    @JoinColumn(name = "maintenance_plan_id", nullable = false)
    private MaintenancePlan maintenancePlan;

    /**
     * 零件图号
     */
    @Column(nullable = true)
    private String partDrawingNumber;

    /**
     * 名称
     */
    @Column(nullable = true)
    private String partName;

    /**
     * 材料
     */
    @Column(nullable = true)
    private String material;

    /**
     * 数量
     */
    @Column(nullable = true)
    private Integer quantity;

    /**
     * 排序序号
     */
    @Column(nullable = true)
    private Integer orderNo;
}

















