package ru.kotomore.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostDTO {

    @NotEmpty(message = "не должен быть пустым")
    private String title;

    private String body;

    private String imageUrl;
}
