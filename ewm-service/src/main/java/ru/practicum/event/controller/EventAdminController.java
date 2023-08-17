package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.AdminEventUpdateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.customconstraint.EventDateConstraint;
import ru.practicum.event.dto.customconstraint.PositiveTimeRange;
import ru.practicum.event.dto.query.EventAdminQuery;
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
@RequestMapping(ADMIN_PATH + EVENT_PATH)
public class EventAdminController {

    private final EventService eventService;
    private final String path = ADMIN_PATH + EVENT_PATH;
    private final String limitHours = ADMIN_TIME_RANGE_LIMIT;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @PositiveTimeRange EventAdminQuery query,
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {} request, from: {}, size: {}.", path, from, size);
        return ResponseEntity.ok(eventService.getEventsAdmin(query, from, size));
    }

    @PatchMapping(EVENT_ID_VAR)
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @EventDateConstraint(limitHours) @RequestBody AdminEventUpdateDto dto) {
        log.info("Received PATCH {}/{} request, dto: {}.", path, eventId, dto);
        return ResponseEntity.ok(eventService.updateEventAdmin(eventId, dto));
    }
}
