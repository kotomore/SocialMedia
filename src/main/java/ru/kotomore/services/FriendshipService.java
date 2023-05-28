package ru.kotomore.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotomore.dto.SuccessMessage;
import ru.kotomore.exceptions.SubscribeException;
import ru.kotomore.exceptions.UserNotFoundException;
import ru.kotomore.models.Friendship;
import ru.kotomore.models.FriendshipStatus;
import ru.kotomore.models.Subscription;
import ru.kotomore.models.User;
import ru.kotomore.repositories.FriendshipRepository;
import ru.kotomore.repositories.SubscriptionRepository;
import ru.kotomore.repositories.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    /**
     * Подписать пользователя на обновления другого
     *
     * @param follower   Подписчик
     * @param following  Пользователь, на которого будет создана подписка
     */
    private void subscribe(User follower, User following) {
        Optional<Subscription> existingSubscription = subscriptionRepository
                .findByFollowerAndFollowing(follower, following);
        if (existingSubscription.isPresent()) {
            throw new SubscribeException("Вы уже подписаны на пользователя");
        }
        Subscription subscription = new Subscription();
        subscription.setFollower(follower);
        subscription.setFollowing(following);
        subscriptionRepository.save(subscription);
    }

    private SuccessMessage createFriendRequest(User sender, User recipient) {
        Friendship friendship = new Friendship();
        friendship.setRecipient(recipient);
        friendship.setSender(sender);
        friendship.setStatus(FriendshipStatus.PENDING);

        subscribe(sender, recipient);

        friendshipRepository.save(friendship);
        return new SuccessMessage("Заявка на добавление данного пользователя в друзья отправлена");
    }

    /**
     * Отправить заявку на добавление в друзья
     *
     * @param user         Пользователь, который отправляет заявку
     * @param recipientId  ID пользователя, которому отправляется заявка
     * @return Сообщение с информацией о статусе заявки
     */
    public SuccessMessage sendFriendRequest(User user, Long recipientId) {
        if (user.getId().equals(recipientId)) {
            throw new SubscribeException("Вы не можете подписаться на самого себя");
        }

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException(recipientId));

        // Поиск уже отправленных заявок на дружбу
        Optional<Friendship> existingFriendship = friendshipRepository.findBySenderAndRecipient(user, recipient);
        if (existingFriendship.isPresent()) {
            throw new SubscribeException("Заявка на добавление данного пользователя в друзья уже отправлена");
        }

        // Если была заявка на дружбу от получателя, то обрабатываем ее
        Optional<Friendship> existingFriendRequest = friendshipRepository.findBySenderAndRecipient(recipient, user);
        if (existingFriendRequest.isPresent()) {
            subscribe(user, recipient);
            changeStatus(existingFriendRequest.get(), FriendshipStatus.ACCEPTED);
            return new SuccessMessage("Заявка на добавление в друзья от данного пользователя одобрена");
        }

        // Иначе создаем новую заявку
        return createFriendRequest(user, recipient);
    }

    private void changeStatus(Friendship friendship, FriendshipStatus newStatus) {
        friendship.setStatus(newStatus);
        friendshipRepository.save(friendship);
    }

    private void removeSubscription(User follower, User following) {
        Optional<Subscription> existingSubscription = subscriptionRepository
                .findByFollowerAndFollowing(follower, following);

        if (existingSubscription.isEmpty()) {
            throw new SubscribeException("Подписка отсутствует");
        }

        subscriptionRepository.delete(existingSubscription.get());
    }

    /**
     * Отменить запрос на дружбу либо удалить из друзей
     *
     * @param user         Пользователь, который отменяет заявку на дружбу
     * @param recipientId  ID пользователя, заявку которого отменяем
     * @return Сообщение о статусе заявки
     */
    public SuccessMessage rejectFriendRequest(User user, Long recipientId) {
        if (user.getId().equals(recipientId)) {
            throw new SubscribeException("Вы не можете отписаться от самого себя");
        }

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException(recipientId));

        Friendship existingFriendRequest = findExistingFriendRequest(user, recipient);

        if (existingFriendRequest.getStatus() == FriendshipStatus.ACCEPTED) {
            removeSubscription(user, recipient);
        }

        changeStatus(existingFriendRequest, FriendshipStatus.REJECTED);
        return new SuccessMessage(existingFriendRequest.getStatus() == FriendshipStatus.ACCEPTED
                ? "Пользователь удален из друзей"
                : "Заявка на добавление в друзья от данного пользователя отклонена");
    }

    private Friendship findExistingFriendRequest(User user, User recipient) {
        return friendshipRepository.findBySenderAndRecipient(recipient, user)
                .or(() -> friendshipRepository.findBySenderAndRecipient(user, recipient))
                .orElseThrow(() -> new SubscribeException("Текущей подписки не обнаружено"));
    }

    /**
     * Получить ID пользователей по статусу дружбы
     *
     * @param user    Пользователь, чьи заявки необходимо получить
     * @param status  Статус заявки (Отменена, Подтверждена, В ожидании)
     * @return Список ID пользователей
     */
    public Set<Long> getFriendsIdByStatus(User user, FriendshipStatus status) {
        return friendshipRepository.findByUserAndStatus(user, status)
                .stream()
                .flatMap(friendship -> Stream.of(friendship.getRecipient().getId(), friendship.getSender().getId()))
                .filter(id -> !Objects.equals(id, user.getId()))
                .collect(Collectors.toSet());
    }
}