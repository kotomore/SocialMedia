package ru.kotomore.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtRequest implements Serializable {

    @NotEmpty(message = "не может быть пустым")
    @Email(message = " должно содержать адрес электронной почты")
    public String email;

    @NotEmpty(message = "не может быть пустым")
    public String password;
}
