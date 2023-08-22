package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.service.LocationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(LOCATIONS_PATH)
public class LocationPublicController {
    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations(
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET request, {} from: {}, size: {}.", LOCATIONS_PATH, from, size);
        return ResponseEntity.ok(locationService.getLocations(from, size));
    }

    @GetMapping(LOCATIONS_ID_VAR)
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable @Positive Long locId) {
        log.info("Received GET request, {}/{}", LOCATIONS_PATH, locId);
        return ResponseEntity.ok(locationService.getLocation(locId));
    }
}
