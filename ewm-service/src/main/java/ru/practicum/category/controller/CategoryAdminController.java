package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(ADMIN_PATH + CATEGORY_PATH)
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final String path = ADMIN_PATH + CATEGORY_PATH;

    @PostMapping
    public ResponseEntity<CategoryDto> saveNewCategory(
            @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received POST {} request, dto: {}.", path, categoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.saveNewCategoryAdmin(categoryDto));
    }

    @PatchMapping(CATEGORY_ID_VAR)
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable @Positive Long catId,
            @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received PATCH {}/{} request, dto: {}.", path, catId, categoryDto);
        return ResponseEntity.ok(categoryService.updateCategoryAdmin(catId, categoryDto));
    }

    @DeleteMapping(CATEGORY_ID_VAR)
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @Positive Long catId) {
        log.info("Received DELETE {}/{} request.", path, catId);
        categoryService.deleteCategoryAdmin(catId);
        return ResponseEntity.noContent().build();
    }
}
