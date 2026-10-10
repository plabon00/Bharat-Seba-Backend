package com.learningportal.user.service;

import com.learningportal.common.exception.BadRequestException;
import com.learningportal.common.exception.ResourceNotFoundException;
import com.learningportal.user.dto.UpdatePasswordRequest;
import com.learningportal.user.dto.UpdateProfileRequest;
import com.learningportal.user.dto.UserResponse;
import com.learningportal.user.entity.User;
import com.learningportal.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getUserProfile(UUID userId) {
        User user = getUser(userId);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUser(userId);
        user.setFullName(request.fullName());
        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl());
        }
        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordRequest request) {
        User user = getUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Incorrect current password.");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
