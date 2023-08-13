package ru.practicum.util.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("Error! User id:%d, not found", userId));
    }
}