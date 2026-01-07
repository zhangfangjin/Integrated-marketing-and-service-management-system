package org.example.rootmanage.remotemonitoring.entity;

/**
 * 数据采集模式枚举
 */
public enum CollectionMode {
    /**
     * 实时采集
     */
    REALTIME("实时采集"),

    /**
     * 轮询采集
     */
    POLLING("轮询采集"),

    /**
     * 手动录入
     */
    MANUAL("手动录入");

    private final String description;

    CollectionMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















