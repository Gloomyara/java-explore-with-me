package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilationsPublic(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationPublic(Long compId);

    CompilationDto saveNewCompilationAdmin(NewCompilationDto dto);

    CompilationDto updateCompilationAdmin(Long compId, UpdateCompilationDto dto);

    void deleteCompilationAdmin(Long compId);
}
