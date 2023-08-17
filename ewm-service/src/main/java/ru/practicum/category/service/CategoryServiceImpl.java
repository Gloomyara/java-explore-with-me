package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.pager.Pager;

import java.util.List;

import static ru.practicum.constants.UtilConstants.CATEGORY;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper = CategoryMapper.INSTANCE;

    @Override
    public CategoryDto saveNewCategoryAdmin(CategoryDto dto) {
        return mapper.toDto(categoryRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public CategoryDto updateCategoryAdmin(Long catId, CategoryDto dto) {
        categoryExistsCheck(catId);
        dto.setId(catId);
        return mapper.toDto(categoryRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public void deleteCategoryAdmin(Long catId) {
        categoryExistsCheck(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesPublic(Integer from, Integer size) {
        return mapper.toDto(categoryRepository.findAll(new Pager(from, size)).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryPublic(Long catId) {
        return mapper.toDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY, catId)));
    }

    private void categoryExistsCheck(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException(CATEGORY, catId);
        }
    }
}
