package ru.practicum.location.dto;

import lombok.Data;
import ru.practicum.event.dto.customconstraint.location.LatValidation;
import ru.practicum.event.dto.customconstraint.location.LonValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewLocationDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String name;
    @NotNull
    @LatValidation
    private Double lat;
    @NotNull
    @LonValidation
    private Double lon;
    @NotNull
    private Double radius;
}
