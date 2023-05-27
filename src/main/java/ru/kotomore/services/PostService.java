package ru.kotomore.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kotomore.dto.CreatePostDTO;
import ru.kotomore.models.Post;
import ru.kotomore.models.Subscription;
import ru.kotomore.models.User;
import ru.kotomore.repositories.PostRepository;
import ru.kotomore.repositories.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;

    public Post createPost(User user, CreatePostDTO createPostDTO) {
        // Создание и сохранение поста
        Post post = new Post();
        post.setUser(user);
        post.setTitle(createPostDTO.getTitle());
        post.setBody(createPostDTO.getBody());
        //TODO: Добавить валидацию
        return postRepository.save(post);
    }

    public Page<Post> getPostsByUser(User user, Pageable pageable) {
        // Получение постов пользователя
        Page<Post> posts = postRepository.findByUser(user, pageable);
        if (posts.isEmpty()) {
            throw new EntityNotFoundException("Посты не найдены для пользователя: " + user.getEmail());
        }
        return postRepository.findByUser(user, pageable);
    }

    public Page<Post> getPostsOfFollowedUsers(User user, Pageable pageable) {

        // Создание списка идентификаторов пользователей, на которых подписан текущий пользователь
        List<User> followingUsers = subscriptionRepository.findByFollower(user)
                .stream()
                .map(Subscription::getFollowing)
                .collect(Collectors.toList());
        // Получение постов пользователей на которых подписан текущий пользователь
        return postRepository.findByUserIn(followingUsers, pageable);
    }

    public Post getPostByUserAndId(User user, Long postId) {
        return postRepository.findByUserAndId(user, postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост с указанным идентификатором не найден"));
    }

    public void updatePost(Post post, String newTitle, String newContent) {
        post.setTitle(newTitle);
        post.setBody(newContent);
        //TODO Add validation
        postRepository.save(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }
}
