package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UserEventUpdateDto;
import ru.practicum.event.dto.customconstraint.EventDateConstraint;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(USERS_PATH)
public class EventPrivateController {
    private final EventService eventService;
    private final String limitHours = USER_TIME_RANGE_LIMIT;

    @GetMapping(USERS_ID_VAR + EVENT_PATH)
    public ResponseEntity<List<EventShortDto>> getShortEvents(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {}/{}/{} request, from: {}, size: {}.",
                USERS_PATH, userId, EVENT_PATH, from, size);
        return ResponseEntity.ok(eventService.getShortEventsPrivate(userId, from, size));
    }

    @GetMapping(USERS_ID_VAR + EVENT_PATH + EVENT_ID_VAR)
    public ResponseEntity<EventFullDto> getFullEvent(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("Received GET {}/{}{}/{} request.",
                USERS_PATH, userId, EVENT_PATH, eventId);
        return ResponseEntity.ok(eventService.getFullEventPrivate(userId, eventId));
    }

    @PostMapping(USERS_ID_VAR + EVENT_PATH)
    public ResponseEntity<EventFullDto> saveNewEvent(
            @PathVariable @Positive Long userId,
            @Valid @EventDateConstraint(limitHours) @RequestBody NewEventDto dto) {
        log.info("Received POST {}/{}{} request, dto: {}.",
                USERS_PATH, userId, EVENT_PATH, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.saveNewEventPrivate(userId, dto));
    }

    @PatchMapping(USERS_ID_VAR + EVENT_PATH + EVENT_ID_VAR)
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @EventDateConstraint(limitHours) @RequestBody UserEventUpdateDto dto) {
        log.info("Received PATCH {}/{}{}/{} request, dto: {}.",
                USERS_PATH, userId, EVENT_PATH, eventId, dto);
        return ResponseEntity.ok(eventService.updateEventPrivate(userId, eventId, dto));
    }
}
