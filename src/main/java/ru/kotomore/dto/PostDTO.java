package ru.kotomore.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    @NotNull(message = "не может быть пустым")
    private Long id;

    @NotEmpty(message = "не может быть пустым")
    private String title;

    private String body;

    private String imageUrl;
}
