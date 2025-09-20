package com.portfolio.service;

import com.portfolio.dto.JwtResponse;
import com.portfolio.dto.LoginRequest;
import com.portfolio.dto.MessageResponse;
import com.portfolio.dto.SignupRequest;
import com.portfolio.entity.User;
import com.portfolio.repository.UserRepository;
import com.portfolio.security.JwtUtils;
import com.portfolio.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public ResponseEntity<MessageResponse> registerUser(SignupRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(MessageResponse.builder().message("Error: Username is already taken!").build());
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body(MessageResponse.builder().message("Error: Email is already in use!").build());
        }
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(MessageResponse.builder().message("User registered successfully!").build());
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        JwtResponse build = JwtResponse
                .builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .token(jwt)
                .email(userDetails.getEmail())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .build();

        return ResponseEntity.ok(build);
    }
}
