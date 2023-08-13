package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.query.EventAdminQuery;
import ru.practicum.event.dto.query.EventPublicQuery;
import ru.practicum.event.enums.SortType;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.client.EwmStatsClient;
import ru.practicum.util.exception.category.CategoryNotFoundException;
import ru.practicum.util.exception.event.EventConstraintException;
import ru.practicum.util.exception.event.EventNotFoundException;
import ru.practicum.util.exception.user.UserNotFoundException;
import ru.practicum.util.pagerequest.PageRequester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.constants.UtilConstants.ADMIN_TIME_RANGE_LIMIT;
import static ru.practicum.event.enums.SortType.VIEWS;
import static ru.practicum.event.enums.State.*;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EwmStatsClient ewmStatsClient;

    @Override
    public List<EventShortDto> getShortEventsPrivate(Long userId, Integer from, Integer size) {
        return toShortDto(eventRepository.findAllByInitiatorId(userId, new PageRequester(from, size)).toList());
    }

    @Override
    public EventFullDto saveNewEventPrivate(Long userId, NewEventDto newEventDto) {
        User initiator = findInitiator(userId);
        Category category = findCategory(newEventDto.getCategoryId());
        Event newEvent = toEntity(newEventDto);
        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        newEvent.setState(PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        return toDto(eventRepository.save(newEvent));
    }

    @Override
    public EventFullDto getFullEventPrivate(Long userId, Long eventId) {
        return toDto(findEvent(userId, eventId));
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId,
                                           Long eventId,
                                           EventUpdateDto updateEvent) {
        eventInitiatorCheck(userId, eventId);
        Event event = findNotPublishedEvent(userId, eventId);
        updateEventStatePrivate(updateEvent, event);
        update(updateEvent, event);
        return toDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, EventUpdateDto updateEvent) {
        Event event = findEvent(eventId);
        eventDateAdminConstraintCheck(event.getEventDate());
        updateEventStateAdmin(updateEvent, event);
        update(updateEvent, event);
        return toDto(eventRepository.save(event));
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
                        query.getRangeStart(),
                        query.getRangeEnd(),
                        new PageRequester(from, size))
                .toList();
        return toDto(setEventsViews(events));
    }

    @Override
    public List<EventShortDto> getShortEventsPublic(EventPublicQuery query,
                                                    Integer from,
                                                    Integer size) {
        List<Event> events = eventRepository
                .findEvents(
                        query.getText(),
                        query.getCategories(),
                        query.getPaid(),
                        query.isOnlyAvailable(),
                        query.getRangeStart(),
                        query.getRangeEnd(),
                        new PageRequester(from, size, Sort.by("eventDate")))
                .toList();
        return toShortDto(eventsSort(setEventsViews(events), query.getSort()));
    }

    @Override
    public EventFullDto getEventPublic(Long eventId) {
        return toDto(setEventsViews(List.of(findPublishedEvent(eventId))).get(0));
    }

    private void updateEventStateAdmin(EventUpdateDto updateDto, Event event) {
        if (Objects.nonNull(updateDto.getStateAction())) {
            switch (updateDto.getStateAction()) {
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
                default:
                    throw new EventConstraintException("Error! Unknown StateAction.");
            }
        }
    }

    private void updateEventStatePrivate(EventUpdateDto updateDto, Event event) {
        if (Objects.nonNull(updateDto.getStateAction())) {
            switch (updateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
                default:
                    throw new EventConstraintException("Error! Unknown StateAction.");
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
        Map<Long, Long> eventIdAndViews = mapViewStats(
                requestViewStats(toEventUrls(events)));
        if (!eventIdAndViews.isEmpty()) {
            events.forEach(event -> event.setViews(eventIdAndViews.getOrDefault(event.getId(), 0L)));
        }
        return events;
    }

    private List<ViewStats> requestViewStats(List<String> eventsUrls) {
        return ewmStatsClient.getViewsStats(
                LocalDateTime.now().minusYears(100),
                LocalDateTime.now(),
                eventsUrls,
                true);
    }

    private List<String> toEventUrls(List<Event> events) {
        List<String> eventUrls = new ArrayList<>();
        events.forEach(event -> eventUrls.add("/events/" + event.getId()));
        return eventUrls;
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

    private Event findPublishedEvent(Long eventId) {
        return eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Event findNotPublishedEvent(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_IdAndStateNot(eventId, userId, PUBLISHED)
                .orElseThrow(() -> new EventConstraintException(
                        "Error! Event already published."));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private User findInitiator(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void eventDateAdminConstraintCheck(LocalDateTime eventDate) {
        if (!eventDate.isAfter(LocalDateTime.now().plusHours(ADMIN_TIME_RANGE_LIMIT))) {
            throw new EventConstraintException(
                    String.format("Error! EventDate should be at least %d hours after the current time.",
                            ADMIN_TIME_RANGE_LIMIT));
        }
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Event findEvent(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(eventId, userId));
    }

    private void eventInitiatorCheck(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiator_Id(eventId, userId)) {
            throw new EventNotFoundException(eventId, userId);
        }
    }

    private void update(EventUpdateDto dto, Event entity) {
        EventMapper.INSTANCE.update(dto, entity);
    }

    private Event toEntity(NewEventDto newEventDto) {
        return EventMapper.INSTANCE.toEntity(newEventDto);
    }

    private EventFullDto toDto(Event event) {
        return EventMapper.INSTANCE.toDto(event);
    }

    private List<EventFullDto> toDto(List<Event> event) {
        return EventMapper.INSTANCE.toDto(event);
    }

    private List<EventShortDto> toShortDto(List<Event> events) {
        return EventMapper.INSTANCE.toShortDto(events);
    }

}
