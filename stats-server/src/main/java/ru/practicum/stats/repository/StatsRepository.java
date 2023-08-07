package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    String uniqueStatsJql = "select new ru.practicum.dto.ViewStats(s.app, s.uri, count(DISTINCT s.ip)) " +
            "from Stats s " +
            "where s.timestamp BETWEEN :start and :end ";

    String statsJql = "select new ru.practicum.dto.ViewStats(s.app, s.uri, count(s)) " +
            "from Stats s " +
            "where s.timestamp BETWEEN :start and :end ";

    @Query(uniqueStatsJql +
            "group by s.app, s.uri " +
            "order by count(DISTINCT s.ip) DESC ")
    List<ViewStats> findALlUniqueViewStats(LocalDateTime start,
                                           LocalDateTime end);

    @Query(uniqueStatsJql +
            "and s.uri in (:uris) " +
            "group by s.app, s.uri " +
            "order by count(DISTINCT s.ip) DESC ")
    List<ViewStats> findALlUniqueViewStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris);

    @Query(statsJql +
            "group by s.app, s.uri " +
            "order by count(s) DESC ")
    List<ViewStats> findALlViewStats(LocalDateTime start,
                                     LocalDateTime end);

    @Query(statsJql +
            "and s.uri in (:uris) " +
            "group by s.app, s.uri " +
            "order by count(s) DESC ")
    List<ViewStats> findALlViewStats(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris);
}