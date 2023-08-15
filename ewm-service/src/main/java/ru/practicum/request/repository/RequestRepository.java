package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.ConfirmedRequestsCount;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    @Query("select r " +
            "from Request r " +
            "JOIN FETCH r.event " +
            "where r.id = :requestId " +
            "and r.requester.id = :requesterId ")
    Optional<Request> findByIdAndRequesterIdWithEvent(Long requestId, Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    @Query("select new ru.practicum.request.model.ConfirmedRequestsCount(r.event.id, COALESCE(count(r), 0)) " +
            "from Request r " +
            "where r.event.id = :eventId " +
            "and (r.status = 'CONFIRMED') " +
            "group by r.event.id " +
            "order by r.event.id ")
    Optional<ConfirmedRequestsCount> findConfirmedRequests(Long eventId);

    @Query("select new ru.practicum.request.model.ConfirmedRequestsCount(r.event.id, COALESCE(count(r), 0)) " +
            "from Request r " +
            "where r.event.id IN (:eventIds) " +
            "and (r.status = 'CONFIRMED') " +
            "group by r.event.id " +
            "order by r.event.id ")
    List<ConfirmedRequestsCount> findConfirmedRequests(List<Long> eventIds);
}
