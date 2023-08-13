package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventUpdateDto {
    @JsonProperty("category")
    private Long categoryId;
    private Integer participantLimit;
    private String annotation;
    private String title;
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Boolean requestModeration;
    private StateAction stateAction;

}
