package ru.kotomore.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotomore.dto.MessageDTO;
import ru.kotomore.security.UserDetails;
import ru.kotomore.services.MessageService;

@RestController
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/sendRequest")
    public ResponseEntity<String> sendOrAcceptFriendRequest(@RequestBody MessageDTO messageDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        String response = messageService.sendMessageRequest(userDetails.user(), messageDTO);
        return ResponseEntity.ok(response);
    }
}
