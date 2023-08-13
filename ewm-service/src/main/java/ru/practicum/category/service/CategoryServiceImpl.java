package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.util.exception.category.CategoryNotFoundException;
import ru.practicum.util.pagerequest.PageRequester;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto saveNewCategoryAdmin(CategoryDto categoryDto) {
        return toDto(categoryRepository.save(toEntity(categoryDto)));
    }

    @Override
    public CategoryDto updateCategoryAdmin(Long catId, CategoryDto categoryDto) {
        categoryExistsCheck(catId);
        categoryDto.setId(catId);
        return toDto(categoryRepository.save(toEntity(categoryDto)));
    }

    @Override
    public void deleteCategoryAdmin(Long catId) {
        categoryExistsCheck(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesPublic(Integer from, Integer size) {
        return toDto(categoryRepository.findAll(new PageRequester(from, size)).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryPublic(Long catId) {
        return toDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId)));
    }

    private void categoryExistsCheck(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new CategoryNotFoundException(catId);
        }
    }

    private Category toEntity(CategoryDto categoryDto) {
        return CategoryMapper.INSTANCE.toEntity(categoryDto);
    }

    private CategoryDto toDto(Category category) {
        return CategoryMapper.INSTANCE.toDto(category);
    }

    private List<CategoryDto> toDto(List<Category> category) {
        return CategoryMapper.INSTANCE.toDto(category);
    }

}
