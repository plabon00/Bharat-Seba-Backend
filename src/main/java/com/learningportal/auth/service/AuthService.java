package com.learningportal.auth.service;

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getFullName(), role);
        // Better way to get full name is from User entity, but let's just query or add it to CustomUserDetails
    }

    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new BadRequestException("Error: Email is already in use!");
        }



        User user = new User();
        user.setFullName(registerRequest.fullName());
        user.setEmail(registerRequest.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        user.setRole(UserRole.USER);
        user.setAccountStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        System.out.println("Password reset requested for: " + email);
    }

    public void resetPassword(String token, String newPassword) {
        userRepository.findByEmail(token.toLowerCase().trim()).ifPresent(user -> {
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

            if (email == null) {
                throw new UnauthorizedException("Firebase token does not contain an email");
            }

            // Check if user exists
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                // Register user
                user = new User();
                user.setEmail(email.toLowerCase());
                user.setFullName(name != null && !name.isEmpty() ? name : "Firebase User");
                user.setPasswordHash(passwordEncoder.encode(uid)); // random password placeholder
                
                UserRole assignedRole = UserRole.USER;
                user.setRole(assignedRole);
                
                user.setAccountStatus(UserStatus.ACTIVE);
                user = userRepository.save(user);
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
