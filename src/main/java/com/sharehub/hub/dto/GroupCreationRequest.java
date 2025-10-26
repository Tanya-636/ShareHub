package com.sharehub.hub.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class GroupCreationRequest {
    @NotBlank
    private String groupName;
}