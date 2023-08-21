package ru.practicum.util.exception.locations;

public class LocationAlreadyExistsException extends RuntimeException {

    public LocationAlreadyExistsException(Double lat, Double lon) {
        super(String.format("Location with lat=%2.6f, lon=%2.6f already exists", lat, lon));
    }

}
