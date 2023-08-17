package ru.practicum.stats.util.exception;

public class NegativeTimeRangeException extends RuntimeException {
    public NegativeTimeRangeException() {
        super("Error! End timestamp is before start.");
    }
}
