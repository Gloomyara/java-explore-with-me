package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    @JsonProperty("category")
    private Long categoryId;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private StateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
