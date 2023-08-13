package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventStatusUpdateDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.enums.Status;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.exception.event.EventConstraintException;
import ru.practicum.util.exception.event.EventNotFoundException;
import ru.practicum.util.exception.request.ConfirmationNotRequiredException;
import ru.practicum.util.exception.request.RequestConstraintException;
import ru.practicum.util.exception.request.RequestNotFoundException;
import ru.practicum.util.exception.user.UserAccessException;
import ru.practicum.util.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.request.enums.Status.CONFIRMED;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsPrivate(Long userId) {
        userExistsCheck(userId);
        return toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto saveNewRequestPrivate(Long userId, Long eventId) {
        userExistsCheck(userId);
        eventExistsCheck(eventId);
        requestExistsCheck(userId, eventId);
        userNotEventInitiatorCheck(userId, eventId);
        eventPublishedCheck(eventId);
        eventParticipationLimitCheck(eventId);
        Event event = findEvent(eventId);
        Status requestStatus;
        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            requestStatus = Status.PENDING;
        } else {
            requestStatus = CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        return toDto(requestRepository.save(
                Request.builder()
                        .created(LocalDateTime.now())
                        .requester(findRequester(userId))
                        .event(event)
                        .status(requestStatus)
                        .build()));
    }

    @Override
    public ParticipationRequestDto cancelRequestPrivate(Long userId, Long requestId) {
        var request = findRequest(userId, requestId);
        if (request.getStatus() == CONFIRMED) {
            var event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        }
        request.setStatus(Status.CANCELED);
        return toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        eventInitiatorCheck(userId, eventId);
        return toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public RequestStatusUpdateDto updateEventRequestsStatusPrivate(Long userId,
                                                                   Long eventId,
                                                                   EventStatusUpdateDto dto) {
        eventExistsCheck(eventId);
        eventInitiatorCheck(userId, eventId);
        eventModerationCheck(eventId);
        eventParticipationLimitCheck(eventId);
        Event event = findEvent(eventId);
        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        for (Request request : requests) {
            if (isParticipationLimitFree(event) && dto.getStatus() == CONFIRMED) {
                confirmRequestStatus(request, event);
                confirmed.add(request);
            } else {
                request.setStatus(Status.REJECTED);
                rejected.add(request);
            }
        }
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        return RequestStatusUpdateDto.builder()
                .confirmedRequests(toDto(confirmed))
                .rejectedRequests(toDto(rejected))
                .build();
    }

    private void confirmRequestStatus(Request request, Event event) {
        if (request.getStatus() == Status.PENDING) {
            request.setStatus(CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            throw new RequestConstraintException(
                    String.format("Error! Request id:%d status should be pending.", request.getId())
            );
        }
    }

    private boolean isParticipationLimitFree(Event event) {
        if (event.getParticipantLimit() == 0) {
            return true;
        } else {
            return event.getParticipantLimit() - event.getConfirmedRequests() > 0;
        }
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private void eventExistsCheck(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
    }

    private void eventModerationCheck(Long eventId) {
        if (eventRepository.existsByIdAndParticipantUnLimitOrRequestModerationFalse(eventId)) {
            throw new ConfirmationNotRequiredException(
                    String.format("Error! Event id:%d moderation is not required.", eventId)
            );
        }
    }

    private void eventInitiatorCheck(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiator_Id(eventId, userId)) {
            throw new UserAccessException(
                    String.format("Error! User id:%d is not event id:%d author.", userId, eventId)
            );
        }
    }

    private void userNotEventInitiatorCheck(Long userId, Long eventId) {
        if (eventRepository.existsByIdAndInitiator_Id(eventId, userId)) {
            throw new RequestConstraintException("Error! Initiator cannot create request for event.");
        }
    }

    private Request findRequest(Long userId, Long requestId) {
        return requestRepository.findByIdAndRequesterIdWithEvent(requestId, userId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
    }

    private User findRequester(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void userExistsCheck(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void requestExistsCheck(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestConstraintException(
                    String.format("Error! Request userId:%d eventId:%d already exist.", userId, eventId));
        }
    }

    private void eventPublishedCheck(Long eventId) {
        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED)) {
            throw new RequestConstraintException(
                    String.format("Error! Event id:%d is not published", eventId));
        }
    }

    private void eventParticipationLimitCheck(Long eventId) {
        if (!eventRepository.existsByIdAndParticipantLimitNotReached(eventId)) {
            throw new EventConstraintException(
                    String.format("Error! Event id:%d participation limit reached.", eventId));
        }
    }

    private ParticipationRequestDto toDto(Request request) {
        return RequestMapper.INSTANCE.toDto(request);
    }

    private List<ParticipationRequestDto> toDto(List<Request> request) {
        return RequestMapper.INSTANCE.toDto(request);
    }

}
