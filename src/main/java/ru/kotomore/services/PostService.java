package ru.kotomore.services;

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

    /**
     * Создание нового поста
     *
     * @param user           Пользователь создающий пост
     * @param createPostDTO  DTO содержащий информацию для поста
     * @return созданный пост
     */
    public Post createPost(User user, CreatePostDTO createPostDTO) {
        // Создание и сохранение поста
        Post post = new Post();
        post.setUser(user);
        post.setTitle(createPostDTO.getTitle());
        post.setBody(createPostDTO.getBody());
        post.setImageUrl(createPostDTO.getImageUrl());

        return postRepository.save(post);
    }

    /**
     * Возвращает посты пользователя
     *
     * @param user      Пользователь для которого нужно отобразить посты
     * @param pageable  Класс содержащий информацию о пагинации и сортировке
     * @return Посты пользователя
     */
    public Page<Post> getPostsByUser(User user, Pageable pageable) {
        // Получение постов пользователя
        return postRepository.findByUser(user, pageable);
    }

    /**
     * Возвращает посты людей, на которых подписан пользователь
     *
     * @param user      Пользователь для которого нужно отобразить посты
     * @param pageable  Класс содержащий информацию о пагинации и сортировке
     * @return Посты людей, на которых подписан пользователь
     */
    public Page<Post> getPostsOfFollowedUsers(User user, Pageable pageable) {

        // Создание списка идентификаторов пользователей, на которых подписан текущий пользователь
        List<User> followingUsers = subscriptionRepository.findByFollower(user)
                .stream()
                .map(Subscription::getFollowing)
                .collect(Collectors.toList());
        // Получение постов пользователей на которых подписан текущий пользователь
        return postRepository.findByUserIn(followingUsers, pageable);
    }

    /**
     * Возвращает пост пользователя по ID
     *
     * @param user    Пользователь для которого нужно отобразить пост
     * @param postId  ID поста пользователя
     * @return Пост пользователя
     */
    public Post getPostByUserAndId(User user, Long postId) {
        return postRepository.findByUserAndId(user, postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост с указанным идентификатором не найден"));
    }

    /**
     * Редактирование поста пользователя
     *
     * @param user    Пользователь, пост которого нужно обновить
     * @param postId  ID обновляемого поста
     * @param createPostDTO  Класс содержащий информацию об измененном посте
     * @return обновленный пост
     */
    public Post updatePost(User user, Long postId, CreatePostDTO createPostDTO) {
        Post post = getPostByUserAndId(user, postId);
        post.setTitle(createPostDTO.getTitle());
        post.setBody(createPostDTO.getBody());
        post.setImageUrl(createPostDTO.getImageUrl());

        return postRepository.save(post);
    }

    /**
     * Возвращает посты пользователя
     *
     * @param user  Пользователь, пост которого нужно удалить
     * @param postId  ID поста подлежащего удалению
     */
    public void deletePost(User user, Long postId) {
        Post post = getPostByUserAndId(user, postId);
        postRepository.delete(post);
    }
}
