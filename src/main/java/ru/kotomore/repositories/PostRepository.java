package ru.kotomore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kotomore.models.Post;
import ru.kotomore.models.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageable);
    Optional<Post> findByUserAndId(User user, Long postId);
    Page<Post> findByUserIn(List<User> users, Pageable pageable);
}