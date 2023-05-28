package ru.kotomore.dto.security;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshJwtRequest {

    @NotEmpty(message = "не может быть пустым")
    private String refreshToken;
}