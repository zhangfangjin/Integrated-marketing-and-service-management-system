package org.example.rootmanage.aftersales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.sql.Date;

/**
 * 售后经验实体类
 * 用于记录每种设备出现过的问题和解决方案
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "after_sales_experience")
public class AfterSalesExperience extends BaseEntity {

    /**
     * 客户名称
     */
    @Column(nullable = true)
    private String customerName;

    /**
     * 设备类型
     */
    @Column(nullable = true)
    private String deviceType;

    /**
     * 案例登记时间
     */
    @Column(nullable = true)
    private Date caseRegistrationDate;

    /**
     * 案例类型
     */
    @Column(nullable = true)
    private String caseType;

    /**
     * 经验总结
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String experienceSummary;

    /**
     * 案例备注
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String caseRemark;

    /**
     * 附件路径
     */
    @Column(nullable = true)
    private String attachment;

    /**
     * 登记人ID
     */
    @Column(nullable = true)
    private java.util.UUID registrantId;

    /**
     * 登记人姓名
     */
    @Column(nullable = true)
    private String registrantName;
}





