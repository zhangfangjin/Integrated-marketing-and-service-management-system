package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.MeasurementType;

import java.util.UUID;

/**
 * 表计检测属性请求DTO
 */
@Data
public class MeterMeasurementRequest {

    /**
     * 检测属性名称
     */
    @NotBlank(message = "检测属性名称不能为空")
    private String measurementName;

    /**
     * 检测属性编码
     */
    @NotBlank(message = "检测属性编码不能为空")
    private String measurementCode;

    /**
     * 检测属性类型
     */
    private MeasurementType measurementType;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数据精度
     */
    private Integer precision;

    /**
     * 最小值
     */
    private Double minValue;

    /**
     * 最大值
     */
    private Double maxValue;

    /**
     * 关联点位ID
     */
    private UUID dataPointId;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}





