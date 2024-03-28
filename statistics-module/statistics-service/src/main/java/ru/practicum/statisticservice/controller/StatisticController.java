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
    private final StatisticService statisticService;
//    public static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateStatisticDto saveHit(@RequestBody @Valid CreateStatisticDto hitDto) {
        log.info("Hit from ip {} created", hitDto.getIp());
        return statisticService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ReadStatisticDto> getStatistics(
            @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get statistics from {} to {} for uri {} unique {}", start, end, uris, unique);
//        LocalDateTime decodeStart = decode(start);
//        LocalDateTime decodeEnd = decode(end);
        checkDateAndThrowException(start, end);
//        log.info("Get statistics from {} to {} for uri {} unique {}", start, decodeEnd, uris, unique);
        return statisticService.getStatistics(start, end, uris, unique);
    }

    private void checkDateAndThrowException(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start after end");
        }
    }
//
//    private LocalDateTime decode(String dateTime) {
//        return LocalDateTime.parse(URLDecoder.decode(dateTime, StandardCharsets.UTF_8), PATTERN);
//    }
}
