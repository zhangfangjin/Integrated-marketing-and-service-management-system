package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 表计属性实体类
 * 用于存储表计的静态属性，如功能、型号、倍率、创建日期等
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_meter_attribute")
public class MeterAttribute extends BaseEntity {

    /**
     * 所属表计ID
     */
    @Column(name = "meter_id", nullable = false)
    private UUID meterId;

    /**
     * 所属表计
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", insertable = false, updatable = false)
    private Meter meter;

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
     * 属性值
     */
    @Column
    private String attributeValue;

    /**
     * 属性类型（STRING-字符串, NUMBER-数值, DATE-日期, BOOLEAN-布尔）
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





