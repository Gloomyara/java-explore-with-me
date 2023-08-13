package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(COMPILATION_PATH)
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) boolean pinned,
            @RequestParam(defaultValue = FROM) Integer from,
            @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Received GET {} request, pinned: {}, from: {}, size: {}.", COMPILATION_PATH, pinned, from, size);
        return ResponseEntity.ok(compilationService.getCompilationsPublic(pinned, from, size));
    }

    @GetMapping(COMPILATION_ID_VAR)
    public ResponseEntity<CompilationDto> getCompilation(
            @PathVariable @Positive Long compId) {
        log.info("Received GET {}/{} request.", COMPILATION_PATH, compId);
        return ResponseEntity.ok(compilationService.getCompilationPublic(compId));
    }
}
