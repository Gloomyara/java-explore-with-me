package ru.practicum.request.service;

import ru.practicum.request.dto.RequestStatusUpdateDtoIn;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDtoOut;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getEventRequestsPrivate(Long userId);

    ParticipationRequestDto saveNewRequestPrivate(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestPrivate(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequestsPrivate(Long userId, Long eventId);

    RequestStatusUpdateDtoOut updateEventRequestsStatusPrivate(Long userId,
                                                               Long eventId,
                                                               RequestStatusUpdateDtoIn dto);

}