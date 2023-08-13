package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventStatusUpdateDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(USERS_PATH)
public class UserRequestPrivateController {
    private final RequestService requestService;

    @GetMapping(USERS_ID_VAR + REQUEST_PATH)
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(
            @PathVariable @Positive Long userId) {
        log.info("Received GET {}/{}{} request.", USERS_PATH, userId, REQUEST_PATH);
        return ResponseEntity.ok(requestService.getEventRequestsPrivate(userId));
    }

    @PostMapping(USERS_ID_VAR + REQUEST_PATH)
    public ResponseEntity<ParticipationRequestDto> saveNewRequest(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId) {
        log.info("Received POST {}/{}{} request, eventId: {}.",
                USERS_PATH, userId, REQUEST_PATH, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.saveNewRequestPrivate(userId, eventId));
    }

    @PatchMapping(USERS_ID_VAR + REQUEST_PATH + "/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("Received DELETE {}/{}{}/{}/cancel request.", USERS_PATH, userId, REQUEST_PATH, requestId);
        return ResponseEntity.ok(requestService.cancelRequestPrivate(userId, requestId));
    }

    @GetMapping(USERS_ID_VAR + EVENT_PATH + EVENT_ID_VAR + REQUEST_PATH)
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("Received GET {}/{}{}/{}{} request.", USERS_PATH, userId, EVENT_PATH, eventId, REQUEST_PATH);
        return ResponseEntity.ok(requestService.getEventRequestsPrivate(userId, eventId));
    }

    @PatchMapping(USERS_ID_VAR + EVENT_PATH + EVENT_ID_VAR + REQUEST_PATH)
    public ResponseEntity<RequestStatusUpdateDto> updateRequestStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventStatusUpdateDto dto) {
        log.info("Received PATCH {}/{}{}/{}{} request, dto: {}.",
                USERS_PATH, userId, EVENT_PATH, eventId, REQUEST_PATH, dto);
        return ResponseEntity.ok(requestService.updateEventRequestsStatusPrivate(userId, eventId, dto));
    }
}
