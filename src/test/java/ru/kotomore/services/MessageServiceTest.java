package ru.kotomore.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.kotomore.dto.MessageDTO;
import ru.kotomore.dto.SuccessMessage;
import ru.kotomore.exceptions.SendMessageException;
import ru.kotomore.models.Message;
import ru.kotomore.models.User;
import ru.kotomore.repositories.MessageRepository;
import ru.kotomore.repositories.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageServiceTest {

    private MessageService messageService;
    private MessageRepository messageRepository;
    private FriendshipService friendshipService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        messageRepository = Mockito.mock(MessageRepository.class);
        friendshipService = Mockito.mock(FriendshipService.class);
        userRepository = Mockito.mock(UserRepository.class);

        messageService = new MessageService(messageRepository, friendshipService, userRepository);
    }

    @Test
    public void sendMessageRequest_ValidRecipient_SuccessMessageReturned() {
        Long userId = 1L;
        Long recipientId = 2L;
        String text = "Hello, friend!";

        User user = new User();
        user.setId(userId);

        User recipient = new User();
        recipient.setId(recipientId);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserId(recipientId);
        messageDTO.setText(text);

        Set<Long> friendIds = new HashSet<>();
        friendIds.add(recipientId);
        Mockito.when(friendshipService.findFriendsByUser(user)).thenReturn(friendIds);
        Mockito.when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        Mockito.when(messageRepository.existsBySenderAndRecipient(user, recipient)).thenReturn(false);

        SuccessMessage result = messageService.sendMessageRequest(user, messageDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Заявка на переписку отправлена", result.getText());
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any(Message.class));
    }

    @Test
    public void sendMessageRequest_UserEqualsRecipient_SendMessageExceptionThrown() {
        Long userId = 1L;
        String text = "Hello, me!";

        User user = new User();
        user.setId(userId);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserId(userId);
        messageDTO.setText(text);

        Assertions.assertThrows(SendMessageException.class, () -> messageService.sendMessageRequest(user, messageDTO));

        Mockito.verify(friendshipService, Mockito.times(0)).findFriendsByUser(Mockito.any(User.class));
        Mockito.verify(userRepository, Mockito.times(0)).findById(Mockito.anyLong());
        Mockito.verify(messageRepository, Mockito.times(0)).existsBySenderAndRecipient(Mockito.any(User.class), Mockito.any(User.class));
    }

    @Test
    public void sendMessageRequest_UserNotFriendsWithRecipient_SendMessageExceptionThrown() {
        Long userId = 1L;
        Long recipientId = 2L;
        String text = "Hello, friend!";

        User user = new User();
        user.setId(userId);

        User recipient = new User();
        recipient.setId(recipientId);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUserId(recipientId);
        messageDTO.setText(text);

        Set<Long> friendIds = new HashSet<>();
        Mockito.when(friendshipService.findFriendsByUser(user)).thenReturn(friendIds);

        Assertions.assertThrows(SendMessageException.class, () -> messageService.sendMessageRequest(user, messageDTO));

        Mockito.verify(userRepository, Mockito.times(0)).findById(Mockito.anyLong());
        Mockito.verify(messageRepository, Mockito.times(0)).existsBySenderAndRecipient(Mockito.any(User.class), Mockito.any(User.class));
    }
}
