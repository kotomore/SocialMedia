package ru.kotomore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kotomore.dto.CreatePostDTO;
import ru.kotomore.dto.PostDTO;
import ru.kotomore.models.Post;
import ru.kotomore.models.User;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.PostService;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Создать пост", description = "Позволяет создать пост для текущего пользователя")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostDTO createpostDTO,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.createPost(userDetails.user(), createpostDTO);
        PostDTO createdPostDTO = modelMapper.map(post, PostDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPostDTO);
    }

    @GetMapping
    @Operation(summary = "Посты текущего пользователя", description = "Отображает посты пользователя," +
            "которые он создал")
    public ResponseEntity<Page<PostDTO>> findUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        Page<Post> userPosts = postService.findPostsByUser(userDetails.user(), pageable);
        if (userPosts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Получаем DTO постов текущего пользователя
            Page<PostDTO> postDTOs = userPosts.map(post -> modelMapper.map(post, PostDTO.class));
            return ResponseEntity.ok(postDTOs);
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Посты определенного пользователя", description = "Отображает посты пользователя," +
            "чей ID указан")
    public ResponseEntity<Page<PostDTO>> findUserPostsByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        User user = new User();
        user.setId(id);
        Page<Post> userPosts = postService.findPostsByUser(user, pageable);
        if (userPosts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Получаем DTO постов пользователя
            Page<PostDTO> postDTOs = userPosts.map(post -> modelMapper.map(post, PostDTO.class));
            return ResponseEntity.ok(postDTOs);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редактирование поста", description = "Позволяет редактировать пост текущего пользователя")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id,
                                              @Valid @RequestBody CreatePostDTO createPostDTO,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        Post savedPost = postService.updatePost(userDetails.user(), id, createPostDTO);
        return ResponseEntity.ok(modelMapper.map(savedPost, PostDTO.class));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление поста", description = "Позволяет удалить пост текущего пользователя.")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(userDetails.user(), id);
        return ResponseEntity.noContent().build();
    }
}
