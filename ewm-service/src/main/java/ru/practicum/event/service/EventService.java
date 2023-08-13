package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.query.EventAdminQuery;
import ru.practicum.event.dto.query.EventPublicQuery;

import java.util.List;

public interface EventService {

    List<EventShortDto> getShortEventsPrivate(Long userId,
                                              Integer from,
                                              Integer size);

    EventFullDto saveNewEventPrivate(Long userId,
                                     NewEventDto newEventDto);

    EventFullDto getFullEventPrivate(Long userId,
                                     Long eventId);

    EventFullDto updateEventPrivate(Long userId,
                                    Long eventId,
                                    EventUpdateDto eventUpdateDto);

    EventFullDto updateEventAdmin(Long eventId,
                                  EventUpdateDto eventUpdateDto);

    List<EventShortDto> getShortEventsPublic(EventPublicQuery eventPublicQuery,
                                             Integer from,
                                             Integer size);

    List<EventFullDto> getEventsAdmin(EventAdminQuery eventAdminQuery,
                                      Integer from,
                                      Integer size);

    EventFullDto getEventPublic(Long eventId);

}
