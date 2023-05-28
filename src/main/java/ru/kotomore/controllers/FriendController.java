package ru.kotomore.controllers;

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
@RequestMapping("/api/v1/friends")
@AllArgsConstructor
public class FriendController {
    private final FriendshipService friendshipService;

    @PostMapping("/add")
    public ResponseEntity<String> sendOrAcceptFriendRequest(@Valid @RequestBody FriendDTO friendDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        String response = friendshipService.sendFriendRequest(userDetails.user(), friendDTO.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> rejectFriendRequest(@Valid @RequestBody FriendDTO friendDTO,
                                                      @AuthenticationPrincipal UserDetails userDetails) {

        String response = friendshipService.rejectFriendRequest(userDetails.user(), friendDTO.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<UserFriendsDTO> getFriendList(@RequestParam FriendshipStatus status,
                                                        @AuthenticationPrincipal UserDetails userDetails) {

        Set<Long> ids = friendshipService.getFriendsIdByStatus(userDetails.user(), status);
        UserFriendsDTO userFriendsDTO = new UserFriendsDTO(status.getDescription(), ids);
        return ResponseEntity.ok(userFriendsDTO);
    }
}
