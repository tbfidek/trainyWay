package com.example.application.data.service;

import com.example.application.data.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends
        JpaRepository<Review, Long>,
        JpaSpecificationExecutor<Review> {
    @Query(value = "select * from \"review_code_verification\"(?1, ?2, ?3)", nativeQuery = true)
    boolean verifyCode(String code, Long id, Long uid);
    @Query(value = "select avg(rating) from reviews where train_id = ?1", nativeQuery = true)
    float ratingScore(Long trainId);
}