package org.example.rootmanage.aftersales.dto;

import lombok.Data;

import java.sql.Date;

/**
 * 完成售后服务请求DTO
 */
@Data
public class CompleteServiceRequest {

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 售后总结
     */
    private String serviceSummary;

    /**
     * 完成日期
     */
    private Date completionDate;
}





