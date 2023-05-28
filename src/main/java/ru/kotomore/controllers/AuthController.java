package ru.kotomore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/registration")
    public ResponseEntity<User> register(@Valid @RequestBody UserDTO userDTO) {
        log.info("Регистрация нового пользователя. Email - " + userDTO.getEmail());
        return ResponseEntity.of(registrationService.saveUser(userDTO));
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody JwtRequest authRequest) {
        log.info("Попытка авторизации. Email - " + authRequest.getEmail());
        return authService.login(authRequest);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh_token(@Valid @RequestBody RefreshJwtRequest request) {
        log.info("Обновление токена");
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/token")
    public JwtResponse getNewAccessToken(@Valid @RequestBody RefreshJwtRequest request) {
        log.info("Получение токена");
        return authService.getAccessToken(request.getRefreshToken());
    }
}
