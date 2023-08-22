package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EventUpdateDto implements EventDtoIn {
    @Size(min = 20, max = 2000)
    protected String annotation;

    @Positive
    @JsonProperty("category")
    protected Long categoryId;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    protected LocalDateTime eventDate;

    @Size(min = 20, max = 7000)
    protected String description;
    protected EventLocationDto location;
    protected Boolean paid;
    protected Boolean requestModeration;
    @PositiveOrZero
    protected Long participantLimit;

    @Size(min = 3, max = 120)
    protected String title;
}
