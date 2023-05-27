package ru.kotomore.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kotomore.dto.FriendDTO;
import ru.kotomore.models.FriendshipStatus;
import ru.kotomore.models.User;
import ru.kotomore.services.FriendshipService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendshipService friendshipService;

    @PostMapping("/add")
    public ResponseEntity<String> sendOrAcceptFriendRequest(@RequestBody FriendDTO friendDTO) {
        String response = friendshipService.sendFriendRequest(getCurrentUser(), friendDTO.getUserId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> rejectFriendRequest(@RequestBody FriendDTO friendDTO) {
        String response = friendshipService.rejectFriendRequest(getCurrentUser(), friendDTO.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<Set<Long>> getFriendList(@RequestParam FriendshipStatus status) {
        Set<Long> response = friendshipService.getUsersIdByStatus(getCurrentUser(), status);

        return ResponseEntity.ok(response);
    }

    private User getCurrentUser() {
        User user = new User();
        user.setId(2L);
        return user;
    }
}
