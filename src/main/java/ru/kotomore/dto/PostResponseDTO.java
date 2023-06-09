package ru.kotomore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {

    private Long id;

    private Long userId;

    private String title;

    private String body;

    private String imageUrl;
}
