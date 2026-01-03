package org.example.rootmanage.module;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.common.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "module")
public class ModuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String zhName;

    @Column(nullable = false)
    private String enName;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer orderNo;

    private String path;

    private String icon;

    private String groupCode;

    /**
     * 用于前端和后端鉴权的标识（通常与 Controller URL 相关）
     */
    private String permissionKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ModuleEntity parent;

    @Column(nullable = false)
    private Boolean parentNode = false;

    @Column(nullable = false)
    private Boolean expanded = false;

    @Column(nullable = false)
    private Boolean visible = true;
}

