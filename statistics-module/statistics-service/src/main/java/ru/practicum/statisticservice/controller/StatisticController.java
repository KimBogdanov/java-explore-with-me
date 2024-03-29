package ru.practicum.statisticservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.statisticservice.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateStatisticDto saveHit(@RequestBody @Valid CreateStatisticDto hitDto) {
        log.info("Hit from ip {} created, time {}", hitDto.getIp(), hitDto.getTimestamp());
        return statisticService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ReadStatisticDto> getStatistics(
            @RequestParam(name = "start") @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get statistics from {} to {} for uri {} unique {}", start, end, uris, unique);
        checkDateAndThrowException(start, end);
        return statisticService.getStatistics(start, end, uris, unique);
    }

    private void checkDateAndThrowException(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start after end");
        }
    }
}
