package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 设备模型属性实体类
 * 用于配置设备模型的自定义属性，如设备的ID、型号、名称等
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_device_model_attribute")
public class DeviceModelAttribute extends BaseEntity {

    /**
     * 所属设备模型ID
     */
    @Column(name = "device_model_id", nullable = false)
    private UUID deviceModelId;

    /**
     * 所属设备模型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id", insertable = false, updatable = false)
    private DeviceModel deviceModel;

    /**
     * 属性名称
     */
    @Column(nullable = false)
    private String attributeName;

    /**
     * 属性编码
     */
    @Column(nullable = false)
    private String attributeCode;

    /**
     * 属性默认值
     */
    @Column
    private String defaultValue;

    /**
     * 属性值类型（STRING-字符串, NUMBER-数值, DATE-日期, BOOLEAN-布尔）
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeValueType valueType = AttributeValueType.STRING;

    /**
     * 属性单位
     */
    @Column
    private String unit;

    /**
     * 是否必填
     */
    @Column(nullable = false)
    private Boolean required = false;

    /**
     * 排序序号
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}





