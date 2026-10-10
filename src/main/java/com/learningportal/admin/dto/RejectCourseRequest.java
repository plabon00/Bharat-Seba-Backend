package com.learningportal.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectCourseRequest(
        @NotBlank String reason
) {}
