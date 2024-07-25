package com.bugboo.BookShop.service;


import com.bugboo.BookShop.domain.Category;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.repository.CategoryRepository;
import com.bugboo.BookShop.type.apiResponse.MetaData;
import com.bugboo.BookShop.type.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //  Get all categories
    public ResponsePagingResultDTO getAllCategories(Pageable pageable) {
        Page<Category> pageCategory = categoryRepository.findAll(pageable);
        MetaData metaData = new MetaData();
        metaData.setCurrentPage(pageCategory.getNumber() + 1);
        metaData.setTotalPages(pageCategory.getTotalPages());
        metaData.setTotalElements(pageCategory.getTotalElements());

        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        responsePagingResultDTO.setMetadata(metaData);
        responsePagingResultDTO.setResult(pageCategory.getContent());
        return responsePagingResultDTO;
    }

    // Create category
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // get category by id
    public Category getCategoryById(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new AppException("Category not found", 404);
        }
        return category;
    }

    // delete category
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

    // update category
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }
}
