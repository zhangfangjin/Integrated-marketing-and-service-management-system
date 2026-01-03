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
 * 去向管理实体
 */
@Getter
@Setter
@Entity
@Table(name = "destination_management")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DestinationManagement extends BaseEntity {

    @Column(nullable = false)
    private String activityName; // 营销活动名称

    @Column(nullable = false)
    private String location; // 地点

    @Column(nullable = false)
    private LocalDateTime time; // 时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user; // 营销人员

    @Column(nullable = false)
    private Boolean submitted = false; // 是否已提交

    private String remark; // 备注
}

