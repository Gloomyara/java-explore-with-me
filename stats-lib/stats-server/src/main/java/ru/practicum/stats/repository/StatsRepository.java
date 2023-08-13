package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("select new ru.practicum.dto.ViewStats(s.app, s.uri, count(DISTINCT s.ip)) " +
            "from Stats s " +
            "where s.timestamp BETWEEN :start and :end " +
            "and ((:uris) is NULL or s.uri IN (:uris)) " +
            "group by s.app, s.uri " +
            "order by count(DISTINCT s.ip) DESC ")
    List<ViewStats> findAllUniqueViewStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris);

    @Query("select new ru.practicum.dto.ViewStats(s.app, s.uri, count(s)) " +
            "from Stats s " +
            "where s.timestamp BETWEEN :start and :end " +
            "and ((:uris) is NULL or s.uri IN (:uris)) " +
            "group by s.app, s.uri " +
            "order by count(s) DESC ")
    List<ViewStats> findAllViewStats(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris);
}