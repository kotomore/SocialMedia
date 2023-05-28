package ru.kotomore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kotomore.dto.FriendDTO;
import ru.kotomore.dto.UserFriendsDTO;
import ru.kotomore.models.FriendshipStatus;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.FriendshipService;

import java.util.Set;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendshipService friendshipService;

    @PostMapping
    @Operation(summary = "Одобряет или создаёт заявку на добавление в друзья")
    public ResponseEntity<String> sendOrAcceptFriendRequest(@Valid @RequestBody FriendDTO friendDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        String response = friendshipService.sendFriendRequest(userDetails.user(), friendDTO.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаляет пользователя из списка друзей или отклоняет заявку в друзья")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        String response = friendshipService.rejectFriendRequest(userDetails.user(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Возвращает список заявок на дружбу", description = "В зависимости от выбранного статуса " +
            "отображает заявки на дружбу (Ожидает подтверждения, принята, отклонена)")
    public ResponseEntity<UserFriendsDTO> getFriendList(@RequestParam FriendshipStatus status,
                                                        @AuthenticationPrincipal UserDetails userDetails) {

        Set<Long> ids = friendshipService.getFriendsIdByStatus(userDetails.user(), status);
        UserFriendsDTO userFriendsDTO = new UserFriendsDTO(status.getDescription(), ids);
        return ResponseEntity.ok(userFriendsDTO);
    }
}
