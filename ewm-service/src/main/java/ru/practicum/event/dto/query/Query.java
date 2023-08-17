package ru.practicum.event.dto.query;

import java.time.LocalDateTime;

public interface Query {
    LocalDateTime getRangeStart();

    LocalDateTime getRangeEnd();
}
