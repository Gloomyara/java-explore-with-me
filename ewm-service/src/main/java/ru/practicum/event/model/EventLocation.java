package ru.practicum.event.model;

import lombok.Data;

@Data
public class EventLocation {
    /**
     * широта
     */
    private Double lat;
    /**
     * долгота
     */
    private Double lon;
}
