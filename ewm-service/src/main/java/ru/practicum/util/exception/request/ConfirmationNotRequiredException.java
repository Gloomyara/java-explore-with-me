package ru.practicum.util.exception.request;

public class ConfirmationNotRequiredException extends RuntimeException {
    public ConfirmationNotRequiredException(String message) {
        super(message);
    }
}
