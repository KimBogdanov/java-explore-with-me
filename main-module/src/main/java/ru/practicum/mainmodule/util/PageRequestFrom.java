package ru.practicum.mainmodule.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Subclass of {@link PageRequest} representing a paginated request with an additional 'from' parameter.
 * This class is used to specify a page request with a starting point.
 */
public class PageRequestFrom extends PageRequest {
    private final int from;

    public PageRequestFrom(int from, int size, Sort sort) {
        super(from / size, size, sort == null ? Sort.unsorted() : sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
