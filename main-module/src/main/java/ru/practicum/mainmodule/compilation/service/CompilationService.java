package ru.practicum.mainmodule.compilation.service;

import ru.practicum.mainmodule.compilation.dto.CompilationDto;
import ru.practicum.mainmodule.compilation.dto.NewCompilationDto;
import ru.practicum.mainmodule.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainmodule.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    CompilationDto getPublicCompilationById(Long compId);

    List<Compilation> getPublicCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);
}
