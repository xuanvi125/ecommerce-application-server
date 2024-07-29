package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Category;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.service.CategoryService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.exception.AppException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //CRUD operations for category

    @ApiMessage("Get all categories successfully")
    @GetMapping("/public/categories")
    public ResponseEntity<ResponsePagingResultDTO> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));

    }

    @ApiMessage("Get category by id successfully")
    @GetMapping("/public/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/admin/categories")
    @ApiMessage("Category added successfully")
    public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }


    @ApiMessage("Category updated successfully")
    @PutMapping("/admin/categories")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category category) {
        Category categoryDB = categoryService.getCategoryById(category.getId());
        if(categoryDB == null){
            throw new AppException("Category not found",400);
        }
        return ResponseEntity.ok(categoryService.updateCategory(category));

    }

    @ApiMessage("Category deleted successfully")
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
