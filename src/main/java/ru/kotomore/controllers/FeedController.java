package ru.kotomore.controllers;

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
import ru.kotomore.dto.PostDTO;
import ru.kotomore.models.Post;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.PostService;

@RestController
@RequestMapping("/api/v1/feed")
@AllArgsConstructor
public class FeedController {
    private final PostService postService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getUserFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        Page<Post> userFeed = postService.getPostsOfFollowedUsers(userDetails.user(), pageable);
        if (userFeed.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Получаем DTO постов для текущего пользователя
            Page<PostDTO> postDTOS = userFeed.map(post -> modelMapper.map(post, PostDTO.class));
            return ResponseEntity.ok(postDTOS);
        }
    }
}
