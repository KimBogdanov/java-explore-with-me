package ru.practicum.statisticservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.commondto.dto.CreateStatisticDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final StatisticClient statisticService;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid CreateStatisticDto hitDto) {
        log.info("Hit from ip {} created", hitDto.getIp());
        return statisticService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get statistics from {} to {} for uri {} unique {}", start, end, uris, unique);
        return statisticService.getStats(start, end, uris, unique);
    }
}
