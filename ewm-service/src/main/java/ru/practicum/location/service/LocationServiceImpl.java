package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.exception.locations.LocationAlreadyExistsException;
import ru.practicum.util.pager.Pager;

import java.util.List;

import static ru.practicum.constants.UtilConstants.LOCATION;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper mapper = LocationMapper.INSTANCE;

    @Override
    public List<LocationDto> getLocations(Integer from, Integer size) {
        return mapper.toDto(locationRepository.findAll(new Pager(from, size, Sort.by("id"))).toList());
    }

    @Override
    public LocationDto getLocation(Long locId) {
        return mapper.toDto(findLocation(locId));
    }

    @Override
    public LocationDto saveNewLocationAdmin(NewLocationDto dto) {
        locationAlreadyExistsCheck(dto.getLat(), dto.getLon());
        return mapper.toDto(locationRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public LocationDto updateLocationAdmin(Long locId, UpdateLocationDto dto) {
        var location = findLocation(locId);
        mapper.update(dto, location);
        return mapper.toDto(locationRepository.save(location));
    }

    @Override
    public void deleteLocationAdmin(Long locId) {
        locationRepository.deleteById(locId);
    }

    private void locationAlreadyExistsCheck(Double lat, Double lon) {
        if (locationRepository.existsByLatAndLon(lat, lon)) {
            throw new LocationAlreadyExistsException(lat, lon);
        }
    }

    private Location findLocation(long locId) {
        return locationRepository.findById(locId)
                .orElseThrow(() -> new EntityNotFoundException(LOCATION, locId));
    }
}
