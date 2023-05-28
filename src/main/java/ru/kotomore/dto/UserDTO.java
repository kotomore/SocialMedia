package ru.kotomore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotEmpty(message = "не может быть пустым")
    @Size(max = 20, message = "не может быть больше 20 символов")
    private String username;

    @NotEmpty(message = "не может быть пустым")
    @Email
    private String email;

    @NotEmpty(message = "не может быть пустым")
    private String password;
}
