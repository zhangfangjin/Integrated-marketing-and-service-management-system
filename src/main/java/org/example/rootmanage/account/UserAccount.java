package org.example.rootmanage.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.role.Role;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserAccount extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String idCard;

    @Column(nullable = false, unique = true)
    private String phone;

    private LocalDate birthDate;

    private String gender;

    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private OptionItem position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private OptionItem region;

    /**
     * 账号名默认为手机号，可修改
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 存储加密后的密码
     */
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.PENDING;

    /**
     * 注册备注或管理员审核备注，可选
     */
    private String remark;
}

