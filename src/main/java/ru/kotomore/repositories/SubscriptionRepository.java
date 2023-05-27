package ru.kotomore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kotomore.models.Subscription;
import ru.kotomore.models.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);
    List<Subscription> findByFollower(User user);
}