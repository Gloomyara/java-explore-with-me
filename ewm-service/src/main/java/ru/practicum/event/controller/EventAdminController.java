package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.query.EventAdminQuery;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN_PATH + EVENT_PATH)
public class EventAdminController {

    private final EventService eventService;
    private final String path = ADMIN_PATH + EVENT_PATH;

    @GetMapping
    public List<EventFullDto> getEvents(
            EventAdminQuery query,
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {} request, from: {}, size: {}.", path, from, size);
        return eventService.getEventsAdmin(query, from, size);
    }

    @PatchMapping(EVENT_ID_VAR)
    public EventFullDto updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventUpdateDto dto) {
        log.info("Received PATCH {}/{} request, dto: {}.", path, eventId, dto);
        return eventService.updateEventAdmin(eventId, dto);
    }
}
