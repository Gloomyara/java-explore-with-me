package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHit;
import ru.practicum.stats.model.Stats;

@UtilityClass
public class StatsMapper {

    public Stats toEntity(EndpointHit dto) {
        if (dto == null) {
            return null;
        }

        Stats stats = new Stats();
        stats.setApp(dto.getApp());
        stats.setUri(dto.getUri());
        stats.setIp(dto.getIp());
        stats.setTimestamp(dto.getTimestamp());

        return stats;
    }

    public EndpointHit toDto(Stats stats) {
        if (stats == null) {
            return null;
        }

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(stats.getApp());
        endpointHit.setUri(stats.getUri());
        endpointHit.setIp(stats.getIp());
        endpointHit.setTimestamp(stats.getTimestamp());

        return endpointHit;
    }
}
