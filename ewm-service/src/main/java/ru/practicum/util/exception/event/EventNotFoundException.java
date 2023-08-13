package ru.practicum.util.exception.event;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super(String.format("Error! Event id:%d, not found.", eventId));
    }

    public EventNotFoundException(Long eventId, Long userId) {
        super(String.format("Error! User id:%d is not Event id:%d initiator.", eventId, userId));
    }
}
