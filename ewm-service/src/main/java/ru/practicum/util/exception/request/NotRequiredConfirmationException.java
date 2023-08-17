package ru.practicum.util.exception.request;

public class NotRequiredConfirmationException extends RuntimeException {
    public NotRequiredConfirmationException(String message) {
        super(message);
    }
}
