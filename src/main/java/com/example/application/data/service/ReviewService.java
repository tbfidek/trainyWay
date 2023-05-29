package com.example.application.data.service;

import com.example.application.data.entity.Review;
import com.example.application.data.entity.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Optional<Review> get(Long id) {
        return repository.findById(id);
    }

    public List<Review> getAll() {
        return repository.findAll();
    }

    public Review update(Review entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Review> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Review> list(Pageable pageable, Specification<Review> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public boolean verifyCode(String code, Long id, Long uid) {
        return repository.verifyCode(code, id, uid);
    }

    public String ratingScore(Long trainId) {
        if (repository.ratingScore(trainId).equals(".00")) {
            return "";
        } else return repository.ratingScore(trainId);
    }

}