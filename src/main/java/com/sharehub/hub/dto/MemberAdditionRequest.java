package com.sharehub.hub.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class MemberAdditionRequest {
    @NotBlank
    @Email
    private String memberEmail;
}