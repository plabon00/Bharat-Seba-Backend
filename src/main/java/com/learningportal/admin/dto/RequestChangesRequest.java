package com.learningportal.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestChangesRequest(
        @NotBlank(message = "Change request reason / feedback is required") 
        String reason
) {}
