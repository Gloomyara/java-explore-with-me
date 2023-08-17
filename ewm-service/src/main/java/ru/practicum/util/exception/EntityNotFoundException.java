package ru.practicum.util.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String s, Long id) {
        super(String.format("Error! %s id:%d, not found.", s, id));
    }

    public EntityNotFoundException(String s1, Long userId, String s2, Long id) {
        super(String.format("Error! %s id:%d is not %s id:%d initiator.", s1, userId, s2, id));
    }
}
