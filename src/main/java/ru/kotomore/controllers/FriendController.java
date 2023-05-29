package ru.kotomore.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kotomore.dto.FriendDTO;
import ru.kotomore.dto.FriendResponseDTO;
import ru.kotomore.dto.SuccessMessage;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendshipService friendshipService;

    @PostMapping
    @Operation(summary = "Одобряет или создаёт заявку на добавление в друзья")
    public ResponseEntity<SuccessMessage> sendOrAcceptFriendRequest(@Valid @RequestBody FriendDTO friendDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        SuccessMessage response = friendshipService.sendFriendRequest(userDetails.user(), friendDTO.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаляет пользователя из списка друзей или отклоняет заявку в друзья")
    public ResponseEntity<SuccessMessage> rejectFriendRequest(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails userDetails) {

        SuccessMessage response = friendshipService.rejectFriendRequest(userDetails.user(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/request")
    @Operation(summary = "Возвращает список полученных заявок на дружбу", description = "В зависимости от выбранного статуса " +
            "отображает заявки на дружбу (Ожидает подтверждения, принята, отклонена)")
    public ResponseEntity<List<FriendResponseDTO>> getFriendRequestList(@AuthenticationPrincipal UserDetails userDetails) {

        List<FriendResponseDTO> ids = friendshipService.findFriendRequestIds(userDetails.user());
        if (ids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/response")
    @Operation(summary = "Возвращает список отправленных заявок на дружбу", description = "В зависимости от выбранного статуса " +
            "отображает заявки на дружбу (Ожидает подтверждения, принята, отклонена)")
    public ResponseEntity<List<FriendResponseDTO>> getFriendResponseList(@AuthenticationPrincipal UserDetails userDetails) {

        List<FriendResponseDTO> ids = friendshipService.findFriendResponseIds(userDetails.user());
        if (ids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ids);
    }
}
