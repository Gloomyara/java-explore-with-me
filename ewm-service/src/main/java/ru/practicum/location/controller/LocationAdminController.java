package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(ADMIN_PATH + LOCATIONS_PATH)
public class LocationAdminController {
    private final LocationService locationService;
    private final String path = ADMIN_PATH + LOCATIONS_PATH;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations(
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {} request, from: {}, size: {}.", path, from, size);
        return ResponseEntity.ok(locationService.getLocations(from, size));
    }

    @GetMapping(LOCATIONS_ID_VAR)
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable @Positive Long locId) {
        log.info("Received GET request, {}/{}", path, locId);
        return ResponseEntity.ok(locationService.getLocation(locId));
    }

    @PostMapping
    public ResponseEntity<LocationDto> saveNewLocation(
            @Valid @RequestBody NewLocationDto dto) {
        log.info("Received POST request, {} dto: {}.", path, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationService.saveNewLocationAdmin(dto));
    }

    @PutMapping(LOCATIONS_ID_VAR)
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable @Positive Long locId,
            @Valid @RequestBody UpdateLocationDto dto) {
        log.info("Received PUT request, {}/{} dto: {}", path, locId, dto);
        return ResponseEntity.ok(locationService.updateLocationAdmin(locId, dto));
    }

    @DeleteMapping(LOCATIONS_ID_VAR)
    public ResponseEntity<Void> deleteLocation(
            @PathVariable @Positive Long locId) {
        log.info("Received DELETE request, {}/{}", path, locId);
        locationService.deleteLocationAdmin(locId);
        return ResponseEntity.noContent().build();
    }
}
