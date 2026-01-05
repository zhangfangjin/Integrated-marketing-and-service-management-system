package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 表计实体类
 * 用于配置各类仪表信息，如水表、电表、压力表等
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_meter")
public class Meter extends BaseEntity {

    /**
     * 表计编号（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String meterCode;

    /**
     * 表计名称
     */
    @Column(nullable = false)
    private String meterName;

    /**
     * 表计类型（WATER-水表, ELECTRIC-电表, PRESSURE-压力表, FLOW-流量计, TEMPERATURE-温度计等）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeterType meterType;

    /**
     * 表计功能描述
     */
    @Column
    private String meterFunction;

    /**
     * 表计型号
     */
    @Column
    private String meterModel;

    /**
     * 表计单位
     */
    @Column
    private String meterUnit;

    /**
     * 表计倍率
     */
    @Column
    private Double multiplier = 1.0;

    /**
     * 安装位置
     */
    @Column
    private String installLocation;

    /**
     * 安装日期
     */
    @Column
    private java.sql.Date installDate;

    /**
     * 所属空间节点ID
     */
    @Column(name = "space_node_id")
    private UUID spaceNodeId;

    /**
     * 所属空间节点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_node_id", insertable = false, updatable = false)
    private SpaceNode spaceNode;

    /**
     * 关联设备ID
     */
    @Column(name = "device_id")
    private UUID deviceId;

    /**
     * 关联设备
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;

    /**
     * 表计属性列表（静态属性）
     */
    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeterAttribute> staticAttributes = new ArrayList<>();

    /**
     * 表计检测属性列表
     */
    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeterMeasurement> measurements = new ArrayList<>();

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 通信协议
     */
    @Column
    private String protocol;

    /**
     * 通信地址
     */
    @Column
    private String commAddress;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}





