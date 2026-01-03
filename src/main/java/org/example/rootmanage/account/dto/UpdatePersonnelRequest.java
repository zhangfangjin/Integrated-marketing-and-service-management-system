package org.example.rootmanage.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdatePersonnelRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String idCard;
    @NotBlank
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private Integer age;
    @NotNull
    private UUID positionId;
    @NotNull
    private UUID regionId;
}

