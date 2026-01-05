package org.example.rootmanage.remotemonitoring.entity;

/**
 * 报警级别枚举
 */
public enum AlarmLevel {
    /**
     * 信息
     */
    INFO("信息", 1),

    /**
     * 警告
     */
    WARNING("警告", 2),

    /**
     * 错误
     */
    ERROR("错误", 3),

    /**
     * 严重
     */
    CRITICAL("严重", 4);

    private final String description;
    private final int priority;

    AlarmLevel(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}





