package ru.kotomore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String body;
    private String imageUrl;
}
