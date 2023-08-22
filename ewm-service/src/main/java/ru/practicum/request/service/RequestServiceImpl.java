package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDtoIn;
import ru.practicum.request.dto.RequestStatusUpdateDtoIn.RequestStatus;
import ru.practicum.request.dto.RequestStatusUpdateDtoOut;
import ru.practicum.request.enums.Status;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ConfirmedRequestsCount;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.exception.event.EventConstraintException;
import ru.practicum.util.exception.request.NotRequiredConfirmationException;
import ru.practicum.util.exception.request.RequestConstraintException;
import ru.practicum.util.exception.user.UserAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.constants.UtilConstants.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper = RequestMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsPrivate(Long userId) {
        userExistsCheck(userId);
        return mapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto saveNewRequestPrivate(Long userId, Long eventId) {
        userExistsCheck(userId);
        eventExistsCheck(eventId);
        requestAlreadyExistsCheck(userId, eventId);
        userNotEventInitiatorCheck(userId, eventId);
        eventPublishedCheck(eventId);
        eventParticipationLimitCheck(eventId);
        Event event = findEvent(eventId);
        Status requestStatus;
        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            requestStatus = Status.PENDING;
        } else {
            requestStatus = Status.CONFIRMED;
        }
        return mapper.toDto(requestRepository.save(
                Request.builder()
                        .created(LocalDateTime.now())
                        .requester(findRequester(userId))
                        .event(event)
                        .status(requestStatus)
                        .build()));
    }

    @Override
    public ParticipationRequestDto cancelRequestPrivate(Long userId, Long requestId) {
        Request request = findRequest(userId, requestId);
        request.setStatus(Status.CANCELED);
        return mapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        eventInitiatorCheck(userId, eventId);
        return mapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public RequestStatusUpdateDtoOut updateEventRequestsStatusPrivate(
            Long userId, Long eventId, RequestStatusUpdateDtoIn dto) {
        eventExistsCheck(eventId);
        eventInitiatorCheck(userId, eventId);
        eventModerationCheck(eventId);
        eventParticipationLimitCheck(eventId);
        Event event = findEvent(eventId);
        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        long limit = event.getParticipantLimit();
        long count = 0L;
        Optional<ConfirmedRequestsCount> optConfirmedRequests = requestRepository.findConfirmedRequests(event.getId());
        if (optConfirmedRequests.isPresent()) {
            count = optConfirmedRequests.get().getCount();
        }
        for (Request request : requests) {
            if (isParticipationLimitFree(limit, count) &&
                    dto.getStatus() == RequestStatus.CONFIRMED) {
                confirmRequestStatus(request);
                confirmed.add(request);
                count -= 1;
            } else {
                request.setStatus(Status.REJECTED);
                rejected.add(request);
            }
        }
        requestRepository.saveAll(requests);
        return RequestStatusUpdateDtoOut.builder()
                .confirmedRequests(mapper.toDto(confirmed))
                .rejectedRequests(mapper.toDto(rejected))
                .build();
    }

    private void confirmRequestStatus(Request request) {
        if (request.getStatus() == Status.PENDING) {
            request.setStatus(Status.CONFIRMED);
        } else {
            throw new RequestConstraintException(
                    String.format("Error! Request id:%d status should be pending.", request.getId())
            );
        }
    }

    private boolean isParticipationLimitFree(long limit, long count) {
        if (limit == 0) {
            return true;
        } else {
            return limit - count > 0;
        }
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(EVENT, eventId));
    }

    private void eventExistsCheck(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(EVENT, eventId);
        }
    }

    private void eventModerationCheck(Long eventId) {
        if (eventRepository.existsByIdAndParticipantUnLimitOrRequestModerationFalse(eventId)) {
            throw new NotRequiredConfirmationException(
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
                .orElseThrow(() -> new EntityNotFoundException(REQUEST, requestId));
    }

    private User findRequester(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USERS, userId));
    }

    private void userExistsCheck(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USERS, userId);
        }
    }

    private void requestAlreadyExistsCheck(Long userId, Long eventId) {
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
}
