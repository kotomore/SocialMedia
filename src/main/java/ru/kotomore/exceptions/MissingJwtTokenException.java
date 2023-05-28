package ru.kotomore.exceptions;

public class MissingJwtTokenException extends RuntimeException {
    public MissingJwtTokenException() {
        super("Missing JWT Token");
    }
}
