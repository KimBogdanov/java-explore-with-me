package ru.practicum.commondto.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ReadStatisticDto {
    private final String app;
    private final String uri;
    private final Integer hits;
}
