package org.example.rootmanage.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CustomerVisitRequest {

    @NotNull(message = "客户ID不能为空")
    private UUID customerId;

    @NotNull(message = "来访时间不能为空")
    private LocalDateTime visitTime;

    private UUID receptionistId;

    @NotNull(message = "来访目的不能为空")
    private String visitPurpose;

    private String visitContent;

    private String visitResult;

    private String remark;
}

