package org.example.rootmanage.remotemonitoring.entity;

/**
 * 检测属性类型枚举
 */
public enum MeasurementType {
    /**
     * 模拟量（如温度、压力等连续变化的量）
     */
    ANALOG("模拟量"),

    /**
     * 数字量（如开关状态等离散值）
     */
    DIGITAL("数字量"),

    /**
     * 累计量（如用电量、用水量等累加值）
     */
    CUMULATIVE("累计量");

    private final String description;

    MeasurementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















