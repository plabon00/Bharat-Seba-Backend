package com.learningportal.admin.dto;

import com.learningportal.user.enums.UserRole;
import com.learningportal.user.enums.UserStatus;

public record UpdateUserStatusRequest(
        UserStatus status
) {}
