package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.defaultclient.DefaultClient;
import ru.practicum.dto.EndpointHit;

import java.util.Map;

public class StatsClient extends DefaultClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getViewStats(Map<String, Object> params) {
        return get("/stats", params);
    }

    public ResponseEntity<Object> saveEndpointHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

}