package ru.kotomore.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotomore.dto.MessageDTO;
import ru.kotomore.dto.SuccessMessage;
import ru.kotomore.exceptions.SendMessageException;
import ru.kotomore.exceptions.UserNotFoundException;
import ru.kotomore.models.FriendshipStatus;
import ru.kotomore.models.Message;
import ru.kotomore.models.User;
import ru.kotomore.repositories.MessageRepository;
import ru.kotomore.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final FriendshipService friendshipService;
    private final UserRepository userRepository;


    /**
     * Отправить запрос на переписку
     *
     * @param user        Пользователь, который отправляет заявку на переписку
     * @param messageDTO  DTO содержащий информацию для отправки запроса на переписку
     * @return Сообщение со статусом заявки
     */
    public SuccessMessage sendMessageRequest(User user, MessageDTO messageDTO) {
        Long recipientId = messageDTO.getUserId();
        String text = messageDTO.getText();

        if (user.getId().equals(recipientId)) {
            throw new SendMessageException("Вы не можете отправить сообщение себе");
        }

        if (!friendshipService.getFriendsIdByStatus(user, FriendshipStatus.ACCEPTED).contains(recipientId)) {
            throw new SendMessageException("Пользователь не добавлен в друзья");
        }

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException(recipientId));

        // Поиск уже отправленных заявок на переписку
        if (messageRepository.existsBySenderAndRecipient(user, recipient)) {
            throw new SendMessageException("Заявка на переписку уже отправлена");
        }

        // Иначе создаем новую заявку
        return createFriendRequest(user, recipient, text);
    }

    private SuccessMessage createFriendRequest(User user, User recipient, String text) {
        Message message = new Message();
        message.setSender(user);
        message.setRecipient(recipient);
        message.setContent(text);
        messageRepository.save(message);
        return new SuccessMessage("Заявка на переписку отправлена");
    }
}