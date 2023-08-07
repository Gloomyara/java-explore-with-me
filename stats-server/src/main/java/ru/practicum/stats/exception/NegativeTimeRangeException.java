package ru.practicum.stats.exception;

public class NegativeTimeRangeException extends IllegalArgumentException {
    public NegativeTimeRangeException() {
        super("Error! End timestamp is before start.");
    }
}
