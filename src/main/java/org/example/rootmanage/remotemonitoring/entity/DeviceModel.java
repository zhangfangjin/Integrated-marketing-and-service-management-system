package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备模型实体类
 * 用于创建不同类型设备的模板，配置设备的基本属性初始值
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_device_model")
public class DeviceModel extends BaseEntity {

    /**
     * 设备模型编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String modelCode;

    /**
     * 设备模型名称
     */
    @Column(nullable = false)
    private String modelName;

    /**
     * 设备类型
     */
    @Column(nullable = false)
    private String deviceType;

    /**
     * 设备品牌
     */
    @Column
    private String brand;

    /**
     * 设备规格
     */
    @Column
    private String specification;

    /**
     * 额定功率
     */
    @Column
    private String ratedPower;

    /**
     * 额定电压
     */
    @Column
    private String ratedVoltage;

    /**
     * 额定电流
     */
    @Column
    private String ratedCurrent;

    /**
     * 额定流量
     */
    @Column
    private String ratedFlow;

    /**
     * 额定扬程
     */
    @Column
    private String ratedHead;

    /**
     * 设备模型属性列表
     */
    @OneToMany(mappedBy = "deviceModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceModelAttribute> attributes = new ArrayList<>();

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





