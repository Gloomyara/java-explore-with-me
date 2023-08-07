package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHit saveHit(EndpointHit endpointHit) {
        return toDto(statsRepository.save(toEntity(endpointHit)));
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        if (uris.isEmpty()) {
            return statsRepository.findALlViewStats(start, end);
        } else {
            return statsRepository.findALlViewStats(start, end, uris);
        }
    }

    @Override
    public List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        if (uris.isEmpty()) {
            return statsRepository.findALlUniqueViewStats(start, end);
        } else {
            return statsRepository.findALlUniqueViewStats(start, end, uris);
        }
    }

    private Stats toEntity(EndpointHit endpointHit) {
        return StatsMapper.toEntity(endpointHit);
    }

    private EndpointHit toDto(Stats stats) {
        return StatsMapper.toDto(stats);
    }
}
