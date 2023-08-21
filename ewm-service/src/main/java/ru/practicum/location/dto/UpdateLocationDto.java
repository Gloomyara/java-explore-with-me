package ru.practicum.location.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateLocationDto {
    @Size(min = 2, max = 255)
    private String name;
    private Double lat;
    private Double lon;
    private Double radius;
}
