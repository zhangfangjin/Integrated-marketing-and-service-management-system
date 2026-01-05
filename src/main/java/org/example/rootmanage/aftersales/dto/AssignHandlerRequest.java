package org.example.rootmanage.aftersales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 指派受理人员请求DTO
 */
@Data
public class AssignHandlerRequest {

    /**
     * 受理人员ID
     */
    @NotNull(message = "受理人员ID不能为空")
    private UUID handlerId;

    /**
     * 受理人员姓名
     */
    private String handlerName;
}





