package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 虚拟表计公式实体类
 * 用于配置虚拟表计的计算公式，支持对多个物理表计的数据进行加减法、加权等运算
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_virtual_meter_formula")
public class VirtualMeterFormula extends BaseEntity {

    /**
     * 公式编码（唯一标识）
     */
    @Column(nullable = false, unique = true)
    private String formulaCode;

    /**
     * 公式名称
     */
    @Column(nullable = false)
    private String formulaName;

    /**
     * 输出点位ID（虚拟表计的计算结果输出点位）
     */
    @Column(name = "output_point_id", nullable = false)
    private UUID outputPointId;

    /**
     * 输出点位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_point_id", insertable = false, updatable = false)
    private DataPoint outputPoint;

    /**
     * 公式表达式（使用点位编码作为变量，如：P001 + P002 * 0.5 - P003）
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String expression;

    /**
     * 公式说明
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 公式参数列表
     */
    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaParameter> parameters = new ArrayList<>();

    /**
     * 计算精度（小数位数）
     */
    @Column
    private Integer precision = 2;

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





