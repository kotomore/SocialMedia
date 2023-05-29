package ru.kotomore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotomore.dto.UserResponseDTO;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.dto.security.JwtRequest;
import ru.kotomore.dto.security.JwtResponse;
import ru.kotomore.dto.security.RefreshJwtRequest;
import ru.kotomore.services.AuthService;
import ru.kotomore.services.RegistrationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;

    @PostMapping("/registration")
    @Operation(summary = "Регистрация нового пользователя", description = "Позволяет создать учетную запись для " +
            "доступа к функционалу приложения. При регистрации необходимо предоставить данные пользователя, " +
            "такие как имя, электронная почта и пароль. После успешной регистрации пользователь " +
            "получает доступ к своему аккаунту и может войти в систему")
    public UserResponseDTO register(@Valid @RequestBody UserDTO userDTO) {
        log.info("Регистрация нового пользователя. Email - " + userDTO.getEmail());
        return modelMapper.map(registrationService.saveUser(userDTO), UserResponseDTO.class);
    }

    @PostMapping("/login")
    @Operation(summary = "Получение API токена для аутентификации", description = "Пользователь предоставляет " +
            "учетные данные (электронную почту и пароль) для получения токена доступа, " +
            "который используется для авторизации запросов")
    public JwtResponse login(@Valid @RequestBody JwtRequest authRequest) {
        log.info("Попытка авторизации. Email - " + authRequest.getEmail());
        return authService.login(authRequest);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление API токена", description = "Позволяет пользователю обновить срок " +
            "действия текущего API токена")
    public JwtResponse refresh_token(@Valid @RequestBody RefreshJwtRequest request) {
        log.info("Обновление токена");
        return authService.refresh(request.getRefreshToken());
    }
}
