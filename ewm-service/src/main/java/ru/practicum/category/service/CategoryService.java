package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto saveNewCategoryAdmin(CategoryDto dto);

    CategoryDto updateCategoryAdmin(Long catId, CategoryDto dto);

    List<CategoryDto> getCategoriesPublic(Integer from, Integer size);

    CategoryDto getCategoryPublic(Long catId);

    void deleteCategoryAdmin(Long catId);

}