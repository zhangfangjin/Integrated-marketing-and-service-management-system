package org.example.rootmanage.remotemonitoring.entity;

/**
 * 公式运算符枚举
 */
public enum FormulaOperator {
    /**
     * 加
     */
    ADD("加", "+"),

    /**
     * 减
     */
    SUBTRACT("减", "-"),

    /**
     * 乘
     */
    MULTIPLY("乘", "*"),

    /**
     * 除
     */
    DIVIDE("除", "/");

    private final String description;
    private final String symbol;

    FormulaOperator(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}





