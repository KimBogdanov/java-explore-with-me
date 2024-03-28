package ru.practicum.statisticservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final StatisticClient statisticService;

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public Mono<Void> saveHit(@RequestBody @Valid CreateStatisticDto hitDto) {
        log.info("Hit from ip {} created", hitDto.getIp());
        return statisticService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public Flux<ReadStatisticDto> getStatistics(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                                @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get statistics from {} to {} for uri {} unique {}", start, end, uris, unique);
        return statisticService.getStatistics(start, end, uris, unique);
    }
}
