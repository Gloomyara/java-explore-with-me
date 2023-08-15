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
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.pagerequest.PageRequester;

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
    public CompilationDto saveNewCompilationAdmin(NewCompilationsDto dto) {
        return toDto(compilationRepository.save(Compilation.builder()
                .events(dto.getEvents().isEmpty() ? new HashSet<>()
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
        update(dto, comp);
        return toDto(compilationRepository.save(comp));
    }

    @Override
    public void deleteCompilationAdmin(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilationsPublic(Boolean pinned, Integer from, Integer size) {
        if (pinned == null) {
            return toDto(compilationRepository.findAll(new PageRequester(from, size)).toList());

        }
        return toDto(compilationRepository.findAllByPinned(pinned, new PageRequester(from, size)).toList());
    }

    @Override
    public CompilationDto getCompilationPublic(Long compId) {
        return toDto(findCompilation(compId));
    }

    private Compilation findCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(COMPILATION, compId));
    }

    private void update(UpdateCompilationDto dto, Compilation compilation) {
        mapper.update(dto, compilation);
    }

    private CompilationDto toDto(Compilation compilation) {
        return mapper.toDto(compilation);
    }

    private List<CompilationDto> toDto(List<Compilation> compilation) {
        return mapper.toDto(compilation);
    }
}
