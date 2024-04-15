package ru.practicum.mainmodule.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    private final List<Long> requestIds;
    @NotNull
    private final RequestStatus status;
}
