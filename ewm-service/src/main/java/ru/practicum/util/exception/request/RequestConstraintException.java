package ru.practicum.util.exception.request;

public class RequestConstraintException extends RuntimeException {
    public RequestConstraintException(String message) {
        super(message);
    }
}
