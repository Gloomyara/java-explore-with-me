package ru.practicum.event.dto.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.enums.SortType;

import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
public class EventPublicQuery implements Query {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private Set<Long> locations;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeEnd = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private boolean onlyAvailable;
    private SortType sort = SortType.EVENT_DATE;
}
