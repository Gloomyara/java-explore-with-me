package ru.practicum.util.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class EwmStatsClient {
    private final String appName;
    private final StatsClient statsClient;

    @Autowired
    public EwmStatsClient(@Value("${spring.application.name}") String appName,
                          StatsClient statsClient) {
        this.appName = appName;
        this.statsClient = statsClient;
    }

    public void saveEndpointHit(HttpServletRequest request) {
        statsClient.saveEndpointHit(
                EndpointHit.builder()
                        .app(appName)
                        .ip(request.getRemoteAddr())
                        .uri(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    public List<ViewStats> getViewsStats(LocalDateTime start,
                                         LocalDateTime end,
                                         List<String> uris,
                                         boolean unique) {
        return statsClient.getViewStats(start, end, uris, unique).getBody();
    }
}
