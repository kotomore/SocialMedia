package ru.kotomore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kotomore.dto.PostResponseDTO;
import ru.kotomore.models.Post;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.PostService;

@RestController
@RequestMapping("/feed")
@AllArgsConstructor
public class FeedController {
    private final PostService postService;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Лента активности пользователя", description = "Отображает посты от" +
            "пользователей, на которых он подписан.")
    public ResponseEntity<Page<PostResponseDTO>> getUserFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, sort, "createdAt");

        Page<Post> userFeed = postService.findPostsOfFollowedUsers(userDetails.user(), pageable);
        if (userFeed.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Получаем DTO постов для текущего пользователя
            Page<PostResponseDTO> postDTOS = userFeed.map(post -> modelMapper.map(post, PostResponseDTO.class));
            return ResponseEntity.ok(postDTOS);
        }
    }
}
