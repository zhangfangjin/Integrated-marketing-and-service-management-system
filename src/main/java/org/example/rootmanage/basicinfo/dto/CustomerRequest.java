package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactEmail;

    private String address;

    private UUID customerTypeId;

    private UUID industryId;

    private String creditCode;

    private String legalRepresentative;

    private String remark;

    private Boolean active = true;
}

