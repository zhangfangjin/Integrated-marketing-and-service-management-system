package org.example.rootmanage.daily.entity;

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

import java.time.LocalDateTime;

/**
 * 周报管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "weekly_report")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WeeklyReport extends BaseEntity {

    @Column(nullable = false)
    private String reportName; // 周报名称

    @Column(nullable = false)
    private LocalDateTime reportTime; // 周报时间

    @Column(columnDefinition = "TEXT")
    private String content; // 周报内容

    @Column(columnDefinition = "TEXT")
    private String remark; // 周报备注

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user; // 创建人

    @Column(nullable = false)
    private Boolean submitted = false; // 是否已提交
}

