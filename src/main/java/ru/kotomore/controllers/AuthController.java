package ru.kotomore.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.dto.security.JwtRequest;
import ru.kotomore.dto.security.JwtResponse;
import ru.kotomore.dto.security.RefreshJwtRequest;
import ru.kotomore.models.User;
import ru.kotomore.services.AuthService;
import ru.kotomore.services.RegistrationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registration")
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        log.info("Register new user with email - " + userDTO.getEmail());
        return ResponseEntity.of(registrationService.saveUser(userDTO));
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest authRequest) {
        log.info("Try authenticate with login - " + authRequest.getEmail());
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        authenticationManager.authenticate(authInputToken);
        return authService.login(authRequest);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh_token(@RequestBody RefreshJwtRequest request) {
        log.info("refresh token");
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/token")
    public JwtResponse getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        log.info("Get access token");
        return authService.getAccessToken(request.getRefreshToken());
    }
}