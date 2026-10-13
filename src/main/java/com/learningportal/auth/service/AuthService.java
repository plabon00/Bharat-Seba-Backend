package com.learningportal.auth.service;

import com.learningportal.auth.dto.CheckEmailResponse;
import com.learningportal.auth.dto.JwtResponse;
import com.learningportal.auth.dto.LoginRequest;
import com.learningportal.auth.dto.RegisterRequest;
import com.learningportal.common.exception.BadRequestException;
import com.learningportal.common.exception.UnauthorizedException;
import com.learningportal.security.CustomUserDetails;
import com.learningportal.security.JwtUtils;
import com.learningportal.user.entity.User;
import com.learningportal.user.enums.UserRole;
import com.learningportal.user.enums.UserStatus;
import com.learningportal.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.learningportal.auth.dto.FirebaseLoginRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        String normalizedEmail = loginRequest.email() != null ? loginRequest.email().trim().toLowerCase() : "";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFullName(), role);
    }

    public void register(RegisterRequest registerRequest) {
        String normalizedEmail = registerRequest.email() != null ? registerRequest.email().trim().toLowerCase() : "";
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("An account with this email already exists. Please log in instead.");
        }

        User user = new User();
        user.setFullName(registerRequest.fullName() != null ? registerRequest.fullName().trim() : "");
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        user.setRole(UserRole.USER);
        user.setAccountStatus(UserStatus.ACTIVE);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("An account with this email already exists. Please log in instead.");
        }
    }

    public CheckEmailResponse checkEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new CheckEmailResponse(false, "", "Email is required");
        }
        String normalizedEmail = email.trim().toLowerCase();
        boolean exists = userRepository.existsByEmailIgnoreCase(normalizedEmail);
        String message = exists
                ? "An account with this email already exists. Please log in instead."
                : "Email is available.";
        return new CheckEmailResponse(exists, normalizedEmail, message);
    }

    public void forgotPassword(String email) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
        System.out.println("Password reset requested for: " + normalizedEmail);
    }

    public void resetPassword(String token, String newPassword) {
        String normalized = token != null ? token.trim().toLowerCase() : "";
        userRepository.findByEmailIgnoreCase(normalized).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });
        System.out.println("Password reset completed with token/email: " + token);
    }

    public JwtResponse firebaseLogin(FirebaseLoginRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.idToken());
            String email = decodedToken.getEmail();
            String uid = decodedToken.getUid();
            String name = decodedToken.getName() != null ? decodedToken.getName() : request.displayName();

            if (email == null || email.trim().isEmpty()) {
                throw new UnauthorizedException("Firebase token does not contain an email");
            }

            String normalizedEmail = email.trim().toLowerCase();

            // Check if user exists (case-insensitive and trimmed)
            User user = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
            if (user == null) {
                // Register user
                user = new User();
                user.setEmail(normalizedEmail);
                user.setFullName(name != null && !name.trim().isEmpty() ? name.trim() : "Firebase User");
                user.setPasswordHash(passwordEncoder.encode(uid)); // random password placeholder
                
                UserRole assignedRole = UserRole.USER;
                user.setRole(assignedRole);
                
                user.setAccountStatus(UserStatus.ACTIVE);
                try {
                    user = userRepository.save(user);
                } catch (DataIntegrityViolationException ex) {
                    // Fallback in case of concurrent first-time registration
                    user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                            .orElseThrow(() -> new BadRequestException("Failed to register user."));
                }
            }

            // Authenticate and generate JWT
            CustomUserDetails userDetails = CustomUserDetails.build(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnauthorizedException("Invalid Firebase token: " + e.getMessage());
        }
    }
}
