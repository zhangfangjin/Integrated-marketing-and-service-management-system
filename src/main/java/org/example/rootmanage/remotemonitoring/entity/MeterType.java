package org.example.rootmanage.remotemonitoring.entity;

/**
 * 表计类型枚举
 */
public enum MeterType {
    /**
     * 水表
     */
    WATER("水表"),

    /**
     * 电表
     */
    ELECTRIC("电表"),

    /**
     * 压力表
     */
    PRESSURE("压力表"),

    /**
     * 流量计
     */
    FLOW("流量计"),

    /**
     * 温度计
     */
    TEMPERATURE("温度计"),

    /**
     * 振动传感器
     */
    VIBRATION("振动传感器"),

    /**
     * 液位计
     */
    LEVEL("液位计"),

    /**
     * 转速计
     */
    SPEED("转速计"),

    /**
     * 功率计
     */
    POWER("功率计"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String description;

    MeterType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





