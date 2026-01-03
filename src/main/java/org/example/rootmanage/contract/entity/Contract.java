package org.example.rootmanage.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Date;

import org.example.rootmanage.common.BaseEntity;

@Data
@Entity
@Table(name = "contract")
public class Contract extends BaseEntity{

    @Column(nullable = false)
    private String contractName;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = true)
    private Date deliveryDate;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String contractRemark;

    @Column(nullable = true)
    private Date scheduleDate;

    @Column(nullable = true)
    private String dilveryStation;

    

}
