package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select count(e) > 0 " +
            "from Event e " +
            "where e.id = :eventId " +
            "and (e.participantLimit = 0 or " +
            "     COALESCE((select count(r) " +
            "      from Request r " +
            "      where r.event.id = :eventId " +
            "      and (r.status = 'CONFIRMED') " +
            "      group by r.event.id), 0) < e.participantLimit) ")
    boolean existsByIdAndParticipantLimitNotReached(Long eventId);

    @Query("select count(e) > 0 " +
            "from Event e " +
            "where e.id = :eventId " +
            "and (e.participantLimit = 0 or e.requestModeration = false) ")
    boolean existsByIdAndParticipantUnLimitOrRequestModerationFalse(Long eventId);

    @Query("select e " +
            "from Event e " +
            "where (e.state = 'PUBLISHED') " +
            "and (:text is NULL " +
            "or lower(e.description) like lower(concat('%', :text, '%')) " +
            "or lower(e.annotation) like lower(concat('%', :text, '%'))) " +
            "and ((:categoryIds) is NULL or e.category.id IN (:categoryIds)) " +
            "and (:paid is NULL or e.paid = :paid) " +
            "and ((:onlyAvailable is TRUE " +
            "     and (e.participantLimit = 0" +
            "     or COALESCE((select count(r) " +
            "        from Request r " +
            "        where r.event.id = e.id " +
            "        and (r.status = 'CONFIRMED') " +
            "        group by r.event.id), 0) < e.participantLimit)) " +
            "     or :onlyAvailable is FALSE) " +
            "and e.eventDate BETWEEN :start and :end " +
            "and ((:locIds) is NULL " +
            "     or EXISTS(select loc.id from Location loc " +
            "        where loc.id IN (:locIds) " +
            "        and function('distance', e.location.lon, e.location.lat, loc.lon, loc.lat) <= loc.radius)) ")
    Page<Event> findEvents(String text,
                           Set<Long> categoryIds,
                           Set<Long> locIds,
                           Boolean paid,
                           boolean onlyAvailable,
                           LocalDateTime start,
                           LocalDateTime end,
                           Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where ((:users) is NULL or e.initiator.id IN (:users)) " +
            "and ((:states) is NULL or e.state IN (:states)) " +
            "and ((:categories) is NULL or e.category.id IN (:categories)) " +
            "and e.eventDate BETWEEN :start and :end " +
            "and ((:locIds) is NULL " +
            "     or EXISTS(select loc.id from Location loc " +
            "        where loc.id IN (:locIds) " +
            "        and function('distance', e.location.lon, e.location.lat, loc.lon, loc.lat) <= loc.radius)) ")
    Page<Event> findEvents(Set<Long> users,
                           Set<State> states,
                           Set<Long> categories,
                           Set<Long> locIds,
                           LocalDateTime start,
                           LocalDateTime end,
                           Pageable pageable);

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(long eventId, long userId);

    boolean existsByIdAndInitiator_Id(long eventId, long userId);

    Optional<Event> findByIdAndInitiator_IdAndStateNot(
            Long eventId, Long initiatorId, State state);

    boolean existsByIdAndState(Long eventId, State state);

    Set<Event> findEventsByIdIn(List<Long> ids);

    Optional<Event> findByIdAndState(Long eventId, State state);

}
