package ru.kotomore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendshipStatus {
    PENDING("Ожидание"),
    ACCEPTED("Принят"),
    REJECTED("Отклонен");

    private final String description;
}
