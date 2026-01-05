package org.example.rootmanage.remotemonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rootmanage.common.BaseEntity;

import java.util.UUID;

/**
 * 公式参数实体类
 * 用于配置虚拟表计公式中的输入参数（物理表计点位）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "remote_formula_parameter")
public class FormulaParameter extends BaseEntity {

    /**
     * 所属公式ID
     */
    @Column(name = "formula_id")
    private UUID formulaId;

    /**
     * 所属公式
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", insertable = false, updatable = false)
    private VirtualMeterFormula formula;

    /**
     * 参数名称（在公式中的变量名）
     */
    @Column(nullable = false)
    private String parameterName;

    /**
     * 关联点位ID
     */
    @Column(name = "data_point_id", nullable = false)
    private UUID dataPointId;

    /**
     * 关联点位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_point_id", insertable = false, updatable = false)
    private DataPoint dataPoint;

    /**
     * 系数/权重
     */
    @Column
    private Double coefficient = 1.0;

    /**
     * 运算符（ADD-加, SUBTRACT-减, MULTIPLY-乘, DIVIDE-除）
     */
    @Column
    @Enumerated(EnumType.STRING)
    private FormulaOperator operator = FormulaOperator.ADD;

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




