package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationsDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(ADMIN_PATH + COMPILATION_PATH)
public class CompilationAdminController {
    private final CompilationService compilationService;
    private final String path = ADMIN_PATH + COMPILATION_PATH;

    @PostMapping
    public ResponseEntity<CompilationDto> saveNewCompilations(
            @Valid @RequestBody NewCompilationsDto newCompilationsDto) {
        log.info("Received GET {} request, dto: {}.", path, newCompilationsDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compilationService.saveNewCompilationAdmin(newCompilationsDto));
    }

    @PatchMapping(COMPILATION_ID_VAR)
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody UpdateCompilationDto dto) {
        log.info("Received PATCH {} request, dto: {}.", path, dto);
        return ResponseEntity.ok(compilationService.updateCompilationAdmin(compId, dto));
    }

    @DeleteMapping(COMPILATION_ID_VAR)
    public ResponseEntity<Void> deleteCompilation(
            @PathVariable @Positive Long compId) {
        log.info("Received DELETE {}/{} request.", path, compId);
        compilationService.deleteCompilationAdmin(compId);
        return ResponseEntity.noContent().build();
    }
}
