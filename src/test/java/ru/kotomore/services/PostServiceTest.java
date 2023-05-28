package ru.kotomore.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.kotomore.dto.CreatePostDTO;
import ru.kotomore.models.Post;
import ru.kotomore.models.User;
import ru.kotomore.repositories.PostRepository;
import ru.kotomore.repositories.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostServiceTest {

    private PostService postService;
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);

        postService = new PostService(postRepository, subscriptionRepository);
    }

    @Test
    public void createPost_ValidData_PostCreated() {
        // Arrange
        Long userId = 1L;
        String title = "Test Post";
        String body = "This is a test post.";
        String imageUrl = "https://example.com/image.jpg";

        User user = new User();
        user.setId(userId);

        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle(title);
        createPostDTO.setBody(body);
        createPostDTO.setImageUrl(imageUrl);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setUser(user);
        savedPost.setTitle(title);
        savedPost.setBody(body);
        savedPost.setImageUrl(imageUrl);

        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(savedPost);

        // Act
        Post result = postService.createPost(user, createPostDTO);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(savedPost, result);
        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any(Post.class));
    }

    @Test
    public void getPostsByUser_ValidUser_ReturnsPageOfPosts() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Pageable pageable = Mockito.mock(Pageable.class);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());

        Page<Post> pageResult = new PageImpl<>(posts);

        Mockito.when(postRepository.findByUser(user, pageable)).thenReturn(pageResult);

        // Act
        Page<Post> result = postService.getPostsByUser(user, pageable);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(pageResult, result);
        Mockito.verify(postRepository, Mockito.times(1)).findByUser(user, pageable);
    }

    @Test
    public void getPostByUserAndId_ValidData_ReturnsPost() {
        // Arrange
        Long userId = 1L;
        Long postId = 1L;

        User user = new User();
        user.setId(userId);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        Mockito.when(postRepository.findByUserAndId(user, postId)).thenReturn(Optional.of(post));

        // Act
        Post result = postService.getPostByUserAndId(user, postId);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(post, result);
        Mockito.verify(postRepository, Mockito.times(1)).findByUserAndId(user, postId);
    }

    @Test
    public void updatePost_ValidData_PostUpdated() {
        // Arrange
        Long userId = 1L;
        Long postId = 1L;
        String newTitle = "Updated Post Title";
        String newBody = "Updated post body.";
        String newImageUrl = "https://example.com/updated-image.jpg";

        User user = new User();
        user.setId(userId);

        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle(newTitle);
        createPostDTO.setBody(newBody);
        createPostDTO.setImageUrl(newImageUrl);

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setUser(user);
        existingPost.setTitle("Old Title");
        existingPost.setBody("Old body.");
        existingPost.setImageUrl("https://example.com/old-image.jpg");

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setUser(user);
        updatedPost.setTitle(newTitle);
        updatedPost.setBody(newBody);
        updatedPost.setImageUrl(newImageUrl);

        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(updatedPost);
        Mockito.when(postRepository.findByUserAndId(user, postId)).thenReturn(Optional.of(existingPost));

        // Act
        Post result = postService.updatePost(user, postId, createPostDTO);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedPost, result);
        Assertions.assertEquals(newTitle, result.getTitle());
        Assertions.assertEquals(newBody, result.getBody());
        Assertions.assertEquals(newImageUrl, result.getImageUrl());
        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any(Post.class));
        Mockito.verify(postRepository, Mockito.times(1)).findByUserAndId(user, postId);
    }

    @Test
    public void deletePost_ValidData_PostDeleted() {
        // Arrange
        Long userId = 1L;
        Long postId = 1L;

        User user = new User();
        user.setId(userId);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        Mockito.when(postRepository.findByUserAndId(user, postId)).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(user, postId);

        // Assert
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
        Mockito.verify(postRepository, Mockito.times(1)).findByUserAndId(user, postId);
    }
}
