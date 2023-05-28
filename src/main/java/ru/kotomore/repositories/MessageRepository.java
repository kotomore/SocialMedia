package ru.kotomore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kotomore.models.Message;
import ru.kotomore.models.User;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    boolean existsBySenderAndRecipient(User sender, User recipient);
}