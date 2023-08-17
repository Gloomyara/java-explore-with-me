package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.event.dto.customconstraint.location.LatValidation;
import ru.practicum.event.dto.customconstraint.location.LonValidation;

@Data
public class LocationDto {
    /**
     * широта
     */
    @LatValidation
    private Double lat;
    /**
     * долгота
     */
    @LonValidation
    private Double lon;
}
