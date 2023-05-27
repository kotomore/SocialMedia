package ru.kotomore.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kotomore.dto.CreatePostDTO;
import ru.kotomore.dto.PostDTO;
import ru.kotomore.models.Post;
import ru.kotomore.models.User;
import ru.kotomore.services.PostService;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostDTO createpostDTO) {
        User user = getCurrentUser(); // Метод для получения текущего пользователя
        Post post = postService.createPost(user, createpostDTO);
        PostDTO createdPostDTO = modelMapper.map(post, PostDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPostDTO);
    }

    @GetMapping("/my")
    public Page<PostDTO> getUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        User user = getCurrentUser();

        // Получаем DTO постов текущего пользователя
        return postService
                .getPostsByUser(user, pageable)
                .map(post -> modelMapper.map(post, PostDTO.class));
    }

    @GetMapping
    public Page<PostDTO> getUserFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        User user = getCurrentUser();
        // Получаем DTO постов текущего пользователя
        return postService
                .getPostsOfFollowedUsers(user, pageable)
                .map(post -> modelMapper.map(post, PostDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO postDTO) {
        User user = getCurrentUser();
        Post post = postService.getPostByUserAndId(user, postDTO.getId());
        postService.updatePost(post, postDTO.getTitle(), postDTO.getBody());
        PostDTO updatedPostDTO = modelMapper.map(post, PostDTO.class);
        return ResponseEntity.ok(updatedPostDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        User user = getCurrentUser();
        Post post = postService.getPostByUserAndId(user, id);
        postService.deletePost(post);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        // Получение текущего пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("John");
        return user;
    }
}
