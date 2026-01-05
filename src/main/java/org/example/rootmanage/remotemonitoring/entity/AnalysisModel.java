package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 分析模型实体类
 * 用于配置设备运行分析的模型，支持按设备或销售对象分类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_analysis_model")
public class AnalysisModel extends BaseEntity {

    /**
     * 分析模型编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String modelCode;

    /**
     * 分析模型名称
     */
    @Column(nullable = false)
    private String modelName;

    /**
     * 分析模型类型（DEVICE-按设备分类, CUSTOMER-按销售对象分类, REGION-按区域分类）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisModelType modelType;

    /**
     * 父模型ID
     */
    @Column(name = "parent_id")
    private UUID parentId;

    /**
     * 父模型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private AnalysisModel parent;

    /**
     * 子模型列表
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisModel> children = new ArrayList<>();

    /**
     * 关联的点位列表
     */
    @OneToMany(mappedBy = "analysisModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisModelPoint> points = new ArrayList<>();

    /**
     * 模型层级
     */
    @Column(nullable = false)
    private Integer level = 1;

    /**
     * 排序序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /**
     * 模型描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}





