package ru.practicum.stats.mapper;

import ru.practicum.dto.EndpointHit;
import ru.practicum.stats.model.Stats;


public class StatsMapper {

    public static Stats toEntity(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }

        Stats stats = new Stats();
        stats.setApp(endpointHit.getApp());
        stats.setUri(endpointHit.getUri());
        stats.setIp(endpointHit.getIp());
        stats.setTimestamp(endpointHit.getTimestamp());

        return stats;
    }

    public static EndpointHit toDto(Stats stats) {
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
