package ru.practicum.util.exception.category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long catId) {
        super(String.format("Error! Category id:%d, not found.", catId));
    }
}