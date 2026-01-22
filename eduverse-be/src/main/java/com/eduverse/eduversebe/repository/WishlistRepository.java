package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Wishlist;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    List<Wishlist> findByUserId(String userId, Sort sort);

    void deleteByUserIdAndCourseId(String userId, String courseId);

    boolean existsByUserIdAndCourseId(String userId, String courseId);

    long countByUserId(String userId);

    List<Wishlist> findByUserId(String userId);
}
