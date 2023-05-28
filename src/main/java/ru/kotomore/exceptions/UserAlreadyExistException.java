package ru.kotomore.exceptions;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String email) {
        super(String.format("Пользователь с email - %s уже существует", email));
    }
}
