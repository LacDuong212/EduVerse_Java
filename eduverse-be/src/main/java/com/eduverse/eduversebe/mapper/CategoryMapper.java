package com.eduverse.eduversebe.mapper;

import com.eduverse.eduversebe.dto.respone.CategoryResponse;
import com.eduverse.eduversebe.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
