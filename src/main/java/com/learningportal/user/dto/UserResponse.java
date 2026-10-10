package com.learningportal.user.dto;

import com.learningportal.user.entity.User;
import com.learningportal.user.enums.UserRole;
import com.learningportal.user.enums.UserStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        UserStatus accountStatus,
        String profileImageUrl,
        ZonedDateTime createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }
}
