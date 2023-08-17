package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constants.UtilConstants.*;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(CATEGORY_PATH)
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("Received GET {} request, from: {}, size: {}.", CATEGORY_PATH, from, size);
        return ResponseEntity.ok(categoryService.getCategoriesPublic(from, size));
    }

    @GetMapping(CATEGORY_ID_VAR)
    public ResponseEntity<CategoryDto> getCategory(
            @PathVariable @Positive Long catId) {
        log.info("Received POST {}/{} request", CATEGORY_PATH, catId);
        return ResponseEntity.ok(categoryService.getCategoryPublic(catId));
    }
}
