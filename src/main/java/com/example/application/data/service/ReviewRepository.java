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
    @Query(value = "select to_char(coalesce(avg(rating), 0), 'FM999999999.00') from reviews where train_id = ?1", nativeQuery = true)
    String ratingScore(Long trainId);
}