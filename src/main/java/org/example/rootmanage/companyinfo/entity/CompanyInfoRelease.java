package org.example.rootmanage.companyinfo.entity;

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
 * 公司信息发布实体
 */
@Getter
@Setter
@Entity
@Table(name = "company_info_release")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyInfoRelease extends BaseEntity {

    @Column(nullable = false)
    private String title; // 标题

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private OptionItem type; // 类型（新产品资料、培训资料、公司政策等）

    @Column(columnDefinition = "TEXT")
    private String content; // 内容

    private String attachments; // 附件（存储附件路径，多个用逗号分隔）

    @Column(nullable = false)
    private Boolean active = true; // 是否启用
}

