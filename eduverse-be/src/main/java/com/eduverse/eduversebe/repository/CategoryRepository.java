package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category,String> {
}
