package org.example.rootmanage.option.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OptionItemRequest {
    @NotBlank
    @JsonProperty("group")
    private String groupCode;
    @NotBlank
    private String title;
    @NotBlank
    private String value;
    @NotNull
    @JsonProperty("order")
    private Integer orderNo;
}

