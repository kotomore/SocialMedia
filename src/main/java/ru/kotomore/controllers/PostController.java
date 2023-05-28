package ru.kotomore.controllers;

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
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.PostService;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostDTO createpostDTO,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.createPost(userDetails.user(), createpostDTO);
        PostDTO createdPostDTO = modelMapper.map(post, PostDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPostDTO);
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), "createdAt");

        Page<Post> userPosts = postService.getPostsByUser(userDetails.user(), pageable);
        if (userPosts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Получаем DTO постов текущего пользователя
            Page<PostDTO> postDTOs = userPosts.map(post -> modelMapper.map(post, PostDTO.class));
            return ResponseEntity.ok(postDTOs);
        }
    }

    @PutMapping
    public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO postDTO,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.getPostByUserAndId(userDetails.user(), postDTO.getId());
        postService.updatePost(post, postDTO.getTitle(), postDTO.getBody());
        PostDTO updatedPostDTO = modelMapper.map(post, PostDTO.class);
        return ResponseEntity.ok(updatedPostDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.getPostByUserAndId(userDetails.user(), id);
        postService.deletePost(post);
        return ResponseEntity.noContent().build();
    }
}
