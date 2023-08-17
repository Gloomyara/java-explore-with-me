package ru.practicum.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.StatsClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {
    private final ObjectMapper objectMapper;

    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(new RestTemplateBuilder(), objectMapper, serverUrl);
    }

}
