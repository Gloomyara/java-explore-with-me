package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationsDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.reposiotry.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.exception.compilation.CompilationNotFoundException;
import ru.practicum.util.pagerequest.PageRequester;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto saveNewCompilationAdmin(NewCompilationsDto dto) {
        return toDto(compilationRepository.save(Compilation.builder()
                .events(eventRepository.findEventsByIdIn(dto.getEvents()))
                .pinned(dto.isPinned())
                .title(dto.getTitle())
                .build()));
    }

    @Override
    public CompilationDto updateCompilationAdmin(Long compId, UpdateCompilationDto dto) {
        Compilation comp = findCompilation(compId);
        if (Objects.nonNull(dto.getEventsIds())) {
            comp.setEvents(eventRepository.findAllById(dto.getEventsIds()));
        }
        update(dto, comp);
        return toDto(compilationRepository.save(comp));
    }

    @Override
    public void deleteCompilationAdmin(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilationsPublic(boolean pinned, Integer from, Integer size) {
        return toDto(compilationRepository.findAllByPinned(pinned, new PageRequester(from, size)).toList());
    }

    @Override
    public CompilationDto getCompilationPublic(Long compId) {
        return toDto(findCompilation(compId));
    }

    private Compilation findCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
    }

    private void update(UpdateCompilationDto dto, Compilation compilation) {
        CompilationMapper.INSTANCE.update(dto, compilation);
    }

    private CompilationDto toDto(Compilation compilation) {
        return CompilationMapper.INSTANCE.toDto(compilation);
    }

    private List<CompilationDto> toDto(List<Compilation> compilation) {
        return CompilationMapper.INSTANCE.toDto(compilation);
    }
}
