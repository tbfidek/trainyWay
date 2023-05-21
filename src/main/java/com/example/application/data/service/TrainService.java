package com.example.application.data.service;

import com.example.application.data.entity.Train;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainService {
    @PersistenceContext
    private EntityManager entityManager;
    private final TrainRepository repository;

    public TrainService(TrainRepository repository) {
        this.repository = repository;
    }

    public Optional<Train> get(Long id) {
        return repository.findById(id);
    }

    public Train update(Train entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Train> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Train> list(Pageable pageable, Specification<Train> filter) {
        return repository.findAll(filter, pageable);
    }
    public Long getMaxTrainId() {
        Query query = entityManager.createQuery("SELECT MAX(t.id) FROM Train t");
        Long maxId = (Long) query.getSingleResult();
        return maxId != null ? maxId : 0;
    }

    public int count() {
        return (int) repository.count();
    }

}
