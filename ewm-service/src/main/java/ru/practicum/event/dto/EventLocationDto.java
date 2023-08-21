package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.event.dto.customconstraint.location.LatValidation;
import ru.practicum.event.dto.customconstraint.location.LonValidation;

import javax.validation.constraints.NotNull;

@Data
public class EventLocationDto {
    /**
     * широта
     */
    @NotNull
    @LatValidation
    private Double lat;
    /**
     * долгота
     */
    @NotNull
    @LonValidation
    private Double lon;
}
