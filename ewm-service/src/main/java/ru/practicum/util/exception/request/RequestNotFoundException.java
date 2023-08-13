package ru.practicum.util.exception.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long requestId) {
        super(String.format("Error! Request id:%d, not found", requestId));
    }
}