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
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.util.exception.event.EventConstraintException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(USERS_PATH)
public class EventPrivateController {
    private final EventService eventService;

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
            @Valid @RequestBody NewEventDto dto) {
        log.info("Received POST {}/{}{} request, dto: {}.",
                USERS_PATH, userId, EVENT_PATH, dto);
        eventDateUserConstraintCheck(dto.getEventDate());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.saveNewEventPrivate(userId, dto));
    }

    @PatchMapping(USERS_ID_VAR + EVENT_PATH + EVENT_ID_VAR)
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventUpdateDto dto) {
        if (Objects.nonNull(dto.getEventDate())) {
            eventDateUserConstraintCheck(dto.getEventDate());
        }
        log.info("Received PATCH {}/{}{}/{} request, dto: {}.",
                USERS_PATH, userId, EVENT_PATH, eventId, dto);
        return ResponseEntity.ok(eventService.updateEventPrivate(userId, eventId, dto));
    }

    private void eventDateUserConstraintCheck(LocalDateTime eventDate) {
        if (!eventDate.isAfter(LocalDateTime.now().plusHours(USER_TIME_RANGE_LIMIT))) {
            throw new EventConstraintException(
                    String.format("Error! EventDate must be at least %d hours after the current time.",
                            USER_TIME_RANGE_LIMIT));
        }
    }
}
