package ru.practicum.location.dto;

import lombok.Data;

@Data
public class LocationDto {
    private Integer id;
    private String name;
    private Double lat;
    private Double lon;
    private Double radius;
}
