package org.example.rootmanage.remotemonitoring.entity;

/**
 * 报警类型枚举
 */
public enum AlarmType {
    /**
     * 超上限报警
     */
    HIGH("超上限"),

    /**
     * 超下限报警
     */
    LOW("超下限"),

    /**
     * 变化率报警
     */
    CHANGE("变化率"),

    /**
     * 状态变化报警
     */
    STATUS("状态变化"),

    /**
     * 上下限双向报警
     */
    RANGE("区间报警");

    private final String description;

    AlarmType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





