package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.constants.UtilConstants.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto implements EventDtoIn {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    @JsonProperty("category")
    private Long categoryId;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private EventLocationDto location;
    @PositiveOrZero
    private long participantLimit = 0L;
    private boolean paid = false;
    private boolean requestModeration = true;

}
