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
 * 客户关键人物实体
 */
@Getter
@Setter
@Entity
@Table(name = "customer_key_person")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomerKeyPerson extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // 所属客户

    @Column(nullable = false)
    private String name; // 联系人名称

    private String code; // 联系人编码

    private String gender; // 性别

    private String salutation; // 称呼

    private String directSuperior; // 直接上级

    private String placeOfOrigin; // 籍贯

    private LocalDate birthday; // 生日

    private String maritalStatus; // 婚姻状况

    private String position; // 职务（决策者、部门主管、普通员工）

    private String hobbies; // 个人爱好

    @Column(nullable = false)
    private Boolean isPrimary = false; // 是否主要联系人

    private String contactInfo; // 联系方式

    private String remark; // 备注
}

