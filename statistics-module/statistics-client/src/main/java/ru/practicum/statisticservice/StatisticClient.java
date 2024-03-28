package ru.practicum.statisticservice;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;


import java.time.LocalDateTime;
import java.util.List;


public interface StatisticClient {
    Mono<Void> saveHit(CreateStatisticDto hitDto);

    Flux<ReadStatisticDto> getStatistics(LocalDateTime start,
                                         LocalDateTime end,
                                         List<String> uris,
                                         Boolean unique);
}
