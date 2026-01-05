package org.example.rootmanage.remotemonitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 曲线数据响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurveDataResponse {

    /**
     * 点位ID
     */
    private UUID dataPointId;

    /**
     * 点位名称
     */
    private String pointName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 曲线颜色
     */
    private String curveColor;

    /**
     * 数据点列表
     */
    private List<DataPointValue> dataPoints;

    /**
     * 数据点值
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPointValue {
        /**
         * 时间
         */
        private LocalDateTime time;

        /**
         * 值
         */
        private Double value;

        /**
         * 平均值
         */
        private Double avgValue;

        /**
         * 最大值
         */
        private Double maxValue;

        /**
         * 最小值
         */
        private Double minValue;
    }
}





