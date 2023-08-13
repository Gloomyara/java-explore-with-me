package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.query.EventPublicQuery;
import ru.practicum.event.service.EventService;
import ru.practicum.util.client.EwmStatsClient;
import ru.practicum.util.exception.event.EventPublicQueryException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(EVENT_PATH)
public class EventPublicController {

    private final EventService eventService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping(EVENT_ID_VAR)
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable @Positive Long eventId,
            HttpServletRequest request) {
        log.info("Received GET {}/{} request.", EVENT_PATH, eventId);
        ewmStatsClient.saveEndpointHit(request);
        return ResponseEntity.ok(eventService.getEventPublic(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getShortEvents(
            @Valid EventPublicQuery query,
            @RequestParam(defaultValue = FROM) Integer from,
            @RequestParam(defaultValue = SIZE) Integer size,
            HttpServletRequest request) {
        validateTimeRange(query.getRangeStart(), query.getRangeEnd());
        log.info("Received GET {} request, query: {}, from: {}, size: {}.", EVENT_PATH, query, from, size);
        ewmStatsClient.saveEndpointHit(request);
        return ResponseEntity.ok(eventService.getShortEventsPublic(query, from, size));
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new EventPublicQueryException("Error! End timestamp is before start.");
        }
    }
}
