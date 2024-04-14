package ru.practicum.mainmodule.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
