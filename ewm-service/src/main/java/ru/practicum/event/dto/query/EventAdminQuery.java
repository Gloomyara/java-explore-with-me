package ru.practicum.event.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.enums.State;

import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminQuery {
    private Set<Long> users;
    private Set<State> states = Set.of(State.PENDING);
    private Set<Long> categories;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeEnd = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
}
