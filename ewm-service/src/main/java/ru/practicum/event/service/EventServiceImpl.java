package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.dto.*;
import ru.practicum.event.dto.query.EventAdminQuery;
import ru.practicum.event.dto.query.EventPublicQuery;
import ru.practicum.event.enums.SortType;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.model.ConfirmedRequestsCount;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.client.EwmStatsClient;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.exception.event.EventConstraintException;
import ru.practicum.util.pager.Pager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constants.UtilConstants.*;
import static ru.practicum.event.enums.SortType.VIEWS;
import static ru.practicum.event.enums.State.*;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EwmStatsClient ewmStatsClient;
    private final EventMapper mapper = EventMapper.INSTANCE;

    @Override
    public List<EventShortDto> getShortEventsPrivate(Long userId, Integer from, Integer size) {
        return setEventsShortDtoConfirmedRequests(
                mapper.toShortDto(eventRepository.findAllByInitiatorId(
                        userId, new Pager(from, size)).toList())
        );
    }

    @Override
    public EventFullDto saveNewEventPrivate(Long userId, NewEventDto dto) {
        User initiator = findInitiator(userId);
        Category category = findCategory(dto.getCategoryId());
        Event newEvent = mapper.toEntity(dto);
        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        newEvent.setState(PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        return setEventsDtoConfirmedRequests(
                mapper.toDto(eventRepository.save(newEvent))
        );
    }

    @Override
    public EventFullDto getFullEventPrivate(Long userId, Long eventId) {
        return setEventsDtoConfirmedRequests(
                mapper.toDto(findEvent(userId, eventId))
        );
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId,
                                           Long eventId,
                                           UserEventUpdateDto dto) {
        eventInitiatorCheck(userId, eventId);
        Event event = findNotPublishedEvent(userId, eventId);
        updateEventStatePrivate(dto, event);
        mapper.update(dto, event);
        return setEventsDtoConfirmedRequests(
                mapper.toDto(eventRepository.save(event))
        );
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, AdminEventUpdateDto dto) {
        Event event = findEvent(eventId);
        updateEventStateAdmin(dto, event);
        mapper.update(dto, event);
        return setEventsDtoConfirmedRequests(
                mapper.toDto(eventRepository.save(event))
        );
    }

    @Override
    public List<EventFullDto> getEventsAdmin(EventAdminQuery query,
                                             Integer from,
                                             Integer size) {
        List<Event> events = eventRepository
                .findEvents(
                        query.getUsers(),
                        query.getStates(),
                        query.getCategories(),
                        query.getLocations(),
                        query.getRangeStart(),
                        query.getRangeEnd(),
                        new Pager(from, size))
                .toList();
        return setEventsDtoConfirmedRequests(
                mapper.toDto(setEventsViews(events))
        );
    }

    @Override
    public List<EventShortDto> getShortEventsPublic(EventPublicQuery query,
                                                    Integer from,
                                                    Integer size) {
        List<Event> events = eventRepository
                .findEvents(
                        query.getText(),
                        query.getCategories(),
                        query.getLocations(),
                        query.getPaid(),
                        query.isOnlyAvailable(),
                        query.getRangeStart(),
                        query.getRangeEnd(),
                        new Pager(from, size, Sort.by("eventDate")))
                .toList();
        return setEventsShortDtoConfirmedRequests(
                mapper.toShortDto(eventsSort(setEventsViews(events), query.getSort()))
        );
    }

    @Override
    public EventFullDto getEventPublic(Long eventId) {
        return setEventsDtoConfirmedRequests(
                mapper.toDto(setEventsViews(List.of(findPublishedEvent(eventId))))
        ).get(0);
    }

    private void updateEventStateAdmin(AdminEventUpdateDto dto, Event event) {
        if (Objects.nonNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() == PENDING) {
                        event.setState(PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new EventConstraintException("Error! Couldn't public Event without PENDING state.");
                    }
                    break;
                case REJECT_EVENT:
                    if (event.getState() != PUBLISHED) {
                        event.setState(CANCELED);
                    } else {
                        throw new EventConstraintException("Error! Couldn't reject PUBLISHED Event.");
                    }
                    break;
            }
        }
    }

    private void updateEventStatePrivate(UserEventUpdateDto dto, Event event) {
        if (Objects.nonNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
            }
        }
    }

    private List<Event> eventsSort(List<Event> events, SortType sortType) {
        if (sortType == VIEWS) {
            events.sort((e1, e2) -> (int) (e2.getViews() - e1.getViews()));
        }
        return events;
    }

    private List<Event> setEventsViews(List<Event> events) {
        Optional<LocalDateTime> start = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
        if (start.isEmpty()) {
            return events;
        }
        Map<Long, Long> eventIdAndViews = mapViewStats(
                requestViewStats(start.get(), toEventUris(events)));
        if (!eventIdAndViews.isEmpty()) {
            events.forEach(event -> event.setViews(eventIdAndViews.getOrDefault(event.getId(), 0L)));
        }
        return events;
    }

    private List<ViewStats> requestViewStats(LocalDateTime start, List<String> eventUris) {

        return ewmStatsClient.getViewsStats(
                start,
                LocalDateTime.now(),
                eventUris,
                true);
    }

    private List<String> toEventUris(List<Event> events) {
        List<String> eventUris = new ArrayList<>();
        events.forEach(event -> eventUris.add("/events/" + event.getId()));
        return eventUris;
    }

    private Map<Long, Long> mapViewStats(List<ViewStats> viewStats) {
        try {
            return viewStats.stream()
                    .filter(vs -> vs.getUri().matches(".*\\d"))
                    .collect(
                            Collectors.toMap(
                                    vs -> Long.parseLong(vs.getUri().substring(
                                            vs.getUri().lastIndexOf("/") + 1)),
                                    ViewStats::getHits)
                    );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error! Check EndpointHit uri's.");
        }
    }

    private EventFullDto setEventsDtoConfirmedRequests(EventFullDto event) {
        return setEventsDtoConfirmedRequests(List.of(event)).get(0);
    }

    private List<EventFullDto> setEventsDtoConfirmedRequests(List<EventFullDto> events) {
        Map<Long, Long> eventIdAndConfirmedRequests = mapConfirmedRequests(
                requestRepository.findConfirmedRequests(events.stream()
                        .map(EventFullDto::getId)
                        .collect(Collectors.toList())));
        if (eventIdAndConfirmedRequests.isEmpty()) {
            events.forEach(event -> event.setConfirmedRequests(0L));
        } else {
            events.forEach(event -> event.setConfirmedRequests(
                    eventIdAndConfirmedRequests.getOrDefault(event.getId(), 0L)));
        }
        return events;
    }

    private List<EventShortDto> setEventsShortDtoConfirmedRequests(List<EventShortDto> events) {
        Map<Long, Long> eventIdAndConfirmedRequests = mapConfirmedRequests(
                requestRepository.findConfirmedRequests(events.stream()
                        .map(EventShortDto::getId)
                        .collect(Collectors.toList())));
        if (eventIdAndConfirmedRequests.isEmpty()) {
            events.forEach(event -> event.setConfirmedRequests(0L));
        } else {
            events.forEach(event -> event.setConfirmedRequests(
                    eventIdAndConfirmedRequests.getOrDefault(event.getId(), 0L)));
        }
        return events;
    }

    private Map<Long, Long> mapConfirmedRequests(List<ConfirmedRequestsCount> confirmedRequests) {
        return confirmedRequests.stream()
                .collect(
                        Collectors.toMap(
                                ConfirmedRequestsCount::getEventId,
                                ConfirmedRequestsCount::getCount)
                );
    }

    private Event findPublishedEvent(Long eventId) {
        return eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(EVENT, eventId));
    }

    private Event findNotPublishedEvent(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_IdAndStateNot(eventId, userId, PUBLISHED)
                .orElseThrow(() -> new EventConstraintException(
                        "Error! Event already published."));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY, categoryId));
    }

    private User findInitiator(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USERS, userId));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(EVENT, eventId));
    }

    private Event findEvent(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(USERS, userId, EVENT, eventId));
    }

    private void eventInitiatorCheck(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiator_Id(eventId, userId)) {
            throw new EntityNotFoundException(USERS, userId, EVENT, eventId);
        }
    }
}
