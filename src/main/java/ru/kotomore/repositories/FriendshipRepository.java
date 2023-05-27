package ru.kotomore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kotomore.models.Friendship;
import ru.kotomore.models.FriendshipStatus;
import ru.kotomore.models.User;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findBySenderAndRecipient(User sender, User recipient);

    @Query("select f from Friendship f where (f.sender = ?1 or f.recipient = ?1) and f.status = ?2")
    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);

}