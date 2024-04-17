package ru.practicum.mainmodule.event.model.enums;

import java.util.Optional;

public enum SearchEventValues {
    EVENT_DATE, VIEWS, UNSORTED;
    public static Optional<SearchEventValues> from(String stringState) {
        for (SearchEventValues state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
