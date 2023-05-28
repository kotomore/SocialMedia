package ru.kotomore.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.kotomore.dto.SuccessMessage;
import ru.kotomore.exceptions.SubscribeException;
import ru.kotomore.exceptions.UserNotFoundException;
import ru.kotomore.models.Friendship;
import ru.kotomore.models.User;
import ru.kotomore.repositories.FriendshipRepository;
import ru.kotomore.repositories.SubscriptionRepository;
import ru.kotomore.repositories.UserRepository;

import java.util.Optional;

public class FriendshipServiceTest {

    private FriendshipService friendshipService;
    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        friendshipRepository = Mockito.mock(FriendshipRepository.class);
        SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        friendshipService = new FriendshipService(friendshipRepository, subscriptionRepository, userRepository);
    }

    @Test
    public void sendFriendRequest_ValidRecipient_SuccessMessageReturned() {
        Long userId = 1L;
        Long recipientId = 2L;

        User user = new User();
        user.setId(userId);

        User recipient = new User();
        recipient.setId(recipientId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        Mockito.when(friendshipRepository.findBySenderAndRecipient(user, recipient)).thenReturn(Optional.empty());
        Mockito.when(friendshipRepository.findBySenderAndRecipient(recipient, user)).thenReturn(Optional.empty());

        SuccessMessage result = friendshipService.sendFriendRequest(user, recipientId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Заявка на добавление данного пользователя в друзья отправлена", result.getText());
        Mockito.verify(friendshipRepository, Mockito.times(1)).save(Mockito.any(Friendship.class));
    }

    @Test
    public void sendFriendRequest_UserEqualsRecipient_SubscribeExceptionThrown() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Assertions.assertThrows(SubscribeException.class, () -> friendshipService.sendFriendRequest(user, userId));

        Mockito.verify(userRepository, Mockito.times(0)).findById(Mockito.anyLong());
        Mockito.verify(friendshipRepository, Mockito.times(0)).findBySenderAndRecipient(Mockito.any(User.class), Mockito.any(User.class));
    }

    @Test
    public void sendFriendRequest_RecipientNotFound_UserNotFoundExceptionThrown() {
        Long userId = 1L;
        Long recipientId = 2L;

        User user = new User();
        user.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(recipientId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> friendshipService.sendFriendRequest(user, recipientId));

        Mockito.verify(friendshipRepository, Mockito.times(0)).findBySenderAndRecipient(Mockito.any(User.class), Mockito.any(User.class));
    }
}
