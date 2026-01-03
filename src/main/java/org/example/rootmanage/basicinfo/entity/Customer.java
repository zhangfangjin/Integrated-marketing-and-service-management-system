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
 * 客户管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "customer")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private String customerName; // 客户名称

    @Column(nullable = false)
    private String contactPerson; // 联系人

    @Column(nullable = false)
    private String contactPhone; // 联系电话

    private String contactEmail; // 联系邮箱

    private String address; // 地址

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_type_id")
    private OptionItem customerType; // 客户类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private OptionItem industry; // 所属行业

    private String creditCode; // 统一社会信用代码

    private String legalRepresentative; // 法定代表人

    private String remark; // 备注

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

