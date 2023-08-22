package ru.practicum.location.dto;

import lombok.Data;
import ru.practicum.event.dto.customconstraint.location.LatValidation;
import ru.practicum.event.dto.customconstraint.location.LonValidation;

import javax.validation.constraints.Size;

@Data
public class UpdateLocationDto {
    @Size(min = 2, max = 255)
    private String name;
    @LatValidation
    private Double lat;
    @LonValidation
    private Double lon;
    private Double radius;
}
