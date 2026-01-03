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
import org.example.rootmanage.option.OptionItem;

/**
 * 销售片区管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "sales_area")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalesArea extends BaseEntity {

    @Column(nullable = false)
    private String areaName; // 片区名称

    @Column(nullable = false, unique = true)
    private String areaCode; // 片区编号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superior_department_id")
    private OptionItem superiorDepartment; // 上级部门

    private String remark; // 备注

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

