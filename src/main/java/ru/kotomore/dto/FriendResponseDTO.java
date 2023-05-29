package ru.kotomore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kotomore.models.FriendshipStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponseDTO {

    private Long userId;

    private FriendshipStatus status;
}
