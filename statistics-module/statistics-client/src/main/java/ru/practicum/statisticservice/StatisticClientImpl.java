package ru.practicum.statisticservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;


import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StatisticClientImpl implements StatisticClient {
    private final WebClient webClient;

    @Autowired
    public StatisticClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public Mono<Void> saveHit(CreateStatisticDto hitDto) {
        return webClient.post()
                .uri("/hit")
                .body(BodyInserters.fromValue(hitDto))
                .retrieve()
                .onStatus(HttpStatus::is2xxSuccessful, response -> Mono.empty())
                .bodyToMono(ErrorResponse.class)
                .flatMap(errorResponse -> Mono.just(errorResponse).then());
    }

    @Override
    public Flux<ReadStatisticDto> getStatistics(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uris,
                                                Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", encode(start))
                        .queryParam("end", encode(end))
                        .queryParamIfPresent("uris", Optional.ofNullable(uris))
                        .queryParamIfPresent("unique", Optional.ofNullable(unique))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(ReadStatisticDto.class);
    }
     private String encode(LocalDateTime dateTime) {
        return URLEncoder.encode(String.valueOf(dateTime), StandardCharsets.UTF_8);
     }
}
