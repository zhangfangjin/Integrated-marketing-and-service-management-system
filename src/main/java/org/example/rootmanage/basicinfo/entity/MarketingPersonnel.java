package org.example.rootmanage.basicinfo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDate;

/**
 * 营销人员管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "marketing_personnel")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MarketingPersonnel extends BaseEntity {

    @Column(nullable = false)
    private String name; // 姓名

    private String gender; // 性别

    private LocalDate birthday; // 生日

    private String contactInfo; // 联系方式

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_area_id")
    private SalesArea responsibleArea; // 负责区域（所属片区）

    private String position; // 职务

    private String remark; // 备注

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

