package ru.practicum.util.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String s, Long catId) {
        super(String.format("Error! %s id:%d, not found.", s, catId));
    }

    public EntityNotFoundException(String s1, Long userId, String s2, Long eventId) {
        super(String.format("Error! %s id:%d is not %s id:%d initiator.", s1, userId, s2, eventId));
    }
}
