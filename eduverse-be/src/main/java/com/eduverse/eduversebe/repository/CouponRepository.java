package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {
    Optional<Coupon> findByCodeAndIsActiveTrue(String code);
}
