package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.reposiotry.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.pager.Pager;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static ru.practicum.constants.UtilConstants.COMPILATION;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper = CompilationMapper.INSTANCE;

    @Override
    public CompilationDto saveNewCompilationAdmin(NewCompilationDto dto) {
        return mapper.toDto(compilationRepository.save(Compilation.builder()
                .events(dto.getEvents() == null || dto.getEvents().isEmpty() ? new HashSet<>()
                        : eventRepository.findEventsByIdIn(dto.getEvents()))
                .pinned(dto.isPinned())
                .title(dto.getTitle())
                .build()));
    }

    @Override
    public CompilationDto updateCompilationAdmin(Long compId, UpdateCompilationDto dto) {
        Compilation comp = findCompilation(compId);
        if (Objects.nonNull(dto.getEventsIds())) {
            comp.setEvents(new HashSet<>((eventRepository.findAllById(dto.getEventsIds()))));
        }
        mapper.update(dto, comp);
        return mapper.toDto(compilationRepository.save(comp));
    }

    @Override
    public void deleteCompilationAdmin(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilationsPublic(Boolean pinned, Integer from, Integer size) {
        if (pinned == null) {
            return mapper.toDto(compilationRepository.findAll(new Pager(from, size)).toList());

        }
        return mapper.toDto(compilationRepository.findAllByPinned(pinned, new Pager(from, size)).toList());
    }

    @Override
    public CompilationDto getCompilationPublic(Long compId) {
        return mapper.toDto(findCompilation(compId));
    }

    private Compilation findCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(COMPILATION, compId));
    }
}
