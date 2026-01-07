package org.example.rootmanage.remotemonitoring.entity;

/**
 * 报警状态枚举
 */
public enum AlarmStatus {
    /**
     * 活动（报警进行中）
     */
    ACTIVE("活动"),

    /**
     * 已确认（人工确认了报警）
     */
    ACKNOWLEDGED("已确认"),

    /**
     * 已恢复（报警条件消除）
     */
    RECOVERED("已恢复");

    private final String description;

    AlarmStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















