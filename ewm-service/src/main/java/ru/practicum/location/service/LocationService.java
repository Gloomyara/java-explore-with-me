package ru.practicum.location.service;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;

import java.util.List;

public interface LocationService {

    List<LocationDto> getLocations(Integer from, Integer size);

    LocationDto getLocation(Long locId);

    LocationDto saveNewLocationAdmin(NewLocationDto dto);

    LocationDto updateLocationAdmin(Long locId, UpdateLocationDto dto);

    void deleteLocationAdmin(Long locId);
}
