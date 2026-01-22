package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.response.CategoryResponse;
import com.eduverse.eduversebe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_ALL_CATEGORIES_SUCCESS,
                categories
        ));
    }
}
