package ru.practicum.commondto.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
@Builder
public class ReadStatisticDto {
    private final String app;
    private final String uri;
    private final Long hits;
}
