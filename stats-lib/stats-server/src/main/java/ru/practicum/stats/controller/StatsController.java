package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.exception.NegativeTimeRangeException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping(STATS_PATH)
    public ResponseEntity<List<ViewStats>> get(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        validateTimeRange(start, end);
        log.info("Received GET {} request, params: {}, {}, {}, {}", STATS_PATH, start, end, uris, unique);
        if (unique) {
            return ResponseEntity.ok(statsService.getUniqueViewStats(start, end, uris));
        }
        return ResponseEntity.ok(statsService.getViewStats(start, end, uris));
    }

    @PostMapping(HIT_PATH)
    public ResponseEntity<EndpointHit> post(@Valid @RequestBody EndpointHit endpointHit) {
        log.info("Received POST {} request, dto: {}.", HIT_PATH, endpointHit);
        return new ResponseEntity<>(statsService.saveHit(endpointHit), HttpStatus.CREATED);
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new NegativeTimeRangeException();
        }
    }
}
