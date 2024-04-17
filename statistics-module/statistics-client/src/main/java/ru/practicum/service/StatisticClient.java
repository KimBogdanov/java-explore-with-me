package ru.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.commondto.dto.CreateStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatisticClient extends BaseClient {
    public static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(CreateStatisticDto stat) {
        return post("/hit", stat);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris, boolean unique) {
        Map<String, Object> parameters;
        String formattedStart = start.format(PATTERN);
        String formattedEnd = end.format(PATTERN);

        if (uris == null) {
            parameters = Map.of("start", formattedStart,
                    "end", formattedEnd,
                    "unique", unique);
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
        parameters = Map.of("start", formattedStart,
                "end", formattedEnd,
                "uris", String.join(",", uris),
                "unique", unique);
        return get("/stats?start={start}&end={end}&unique={unique}&uris={uris}", parameters);
    }
}




