package com.example.application.data.service;

import com.example.application.data.entity.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private final TrainRepository repository;

    public TrainService(TrainRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void resetTrains() {
        repository.resetTrains();
    }

    public Optional<Train> get(Long id) {
        return repository.findById(id);
    }

    public Optional<Train> get(String trainName){
        return repository.findByTrainName(trainName);
    }

    public List<Train> getAll() { return repository.findAll(); }

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

    public int count() {
        return (int) repository.count();
    }
    public List<Train> trainList(String from, String to) {
        return repository.trainList(from ,to);
    }
    public void uploadTrain(String file){ repository.uploadTrain(file);}
}