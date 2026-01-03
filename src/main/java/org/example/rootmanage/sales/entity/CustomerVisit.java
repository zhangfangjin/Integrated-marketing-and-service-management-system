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
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 客户来访管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "customer_visit")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomerVisit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // 客户

    @Column(nullable = false)
    private LocalDateTime visitTime; // 来访时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptionist_id")
    private UserAccount receptionist; // 接待人

    @Column(nullable = false)
    private String visitPurpose; // 来访目的

    private String visitContent; // 来访内容

    private String visitResult; // 来访结果

    private String remark; // 备注
}

