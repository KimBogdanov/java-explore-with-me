package ru.practicum.mainmodule.exception;

import lombok.Getter;
import ru.practicum.mainmodule.util.DateTimeUtil;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String message, String status, String reason) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = DateTimeUtil.formatLocalDateTime(LocalDateTime.now());
    }
}
