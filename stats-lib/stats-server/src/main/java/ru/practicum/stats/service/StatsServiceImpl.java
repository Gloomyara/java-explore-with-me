package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHit saveHit(EndpointHit dto) {
        return StatsMapper.toDto(statsRepository.save(StatsMapper.toEntity(dto)));
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        if (uris.isEmpty()) {
            return statsRepository.findAllViewStats(start, end, null);
        } else {
            return statsRepository.findAllViewStats(start, end, uris);
        }
    }

    @Override
    public List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        if (uris.isEmpty()) {
            return statsRepository.findAllUniqueViewStats(start, end, null);
        } else {
            return statsRepository.findAllUniqueViewStats(start, end, uris);
        }
    }
}
