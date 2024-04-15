package ru.practicum.mainmodule.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainmodule.compilation.dto.CompilationDto;
import ru.practicum.mainmodule.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getPublicCompilationById(@PathVariable Long compId) {
        log.info("getPublicCompilationById compilation id: {}", compId);
        return compilationService.getPublicCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getPublicCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("getPublicCompilations pinned: {} from: {} size: {}", pinned, from, size);
        return compilationService.getPublicCompilations(pinned, from, size);
    }
}
