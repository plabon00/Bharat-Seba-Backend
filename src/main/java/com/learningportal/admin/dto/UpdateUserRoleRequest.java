package com.learningportal.admin.dto;

import com.learningportal.user.enums.UserRole;

public record UpdateUserRoleRequest(
        UserRole role
) {}
