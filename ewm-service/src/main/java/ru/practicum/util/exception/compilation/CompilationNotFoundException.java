package ru.practicum.util.exception.compilation;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(Long compId) {
        super(String.format("Error! Compilation id:%d, not found.", compId));
    }
}