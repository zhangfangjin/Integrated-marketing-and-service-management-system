package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 空间模型节点实体类
 * 用于配置销售对象公司、设备安装区域等树形架构
 * 支持多层次结构：销售对象公司 -> 设备安装区域 -> 设备
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_space_node")
public class SpaceNode extends BaseEntity {

    /**
     * 节点编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String nodeCode;

    /**
     * 节点名称
     */
    @Column(nullable = false)
    private String nodeName;

    /**
     * 节点类型（COMPANY-公司, REGION-区域, BUILDING-建筑, FLOOR-楼层, ROOM-房间）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceNodeType nodeType;

    /**
     * 父节点ID
     */
    @Column(name = "parent_id")
    private UUID parentId;

    /**
     * 父节点（自关联）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private SpaceNode parent;

    /**
     * 子节点列表
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpaceNode> children = new ArrayList<>();

    /**
     * 节点层级（从1开始）
     */
    @Column(nullable = false)
    private Integer level = 1;

    /**
     * 排序序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /**
     * 联系人
     */
    @Column
    private String contactPerson;

    /**
     * 联系电话
     */
    @Column
    private String contactPhone;

    /**
     * 地址
     */
    @Column
    private String address;

    /**
     * 经度
     */
    @Column
    private Double longitude;

    /**
     * 纬度
     */
    @Column
    private Double latitude;

    /**
     * 节点描述
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





