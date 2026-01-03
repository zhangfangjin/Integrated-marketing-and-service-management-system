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
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.option.OptionItem;

/**
 * 团队信息管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "team")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Team extends BaseEntity {

    @Column(nullable = false)
    private String teamName; // 团队名称

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private UserAccount leader; // 团队负责人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private OptionItem department; // 所属部门

    private String description; // 团队描述

    private String remark; // 备注

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

