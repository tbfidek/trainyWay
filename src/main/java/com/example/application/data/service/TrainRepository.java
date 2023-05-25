package com.example.application.data.service;

import com.example.application.data.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface TrainRepository extends
        JpaRepository<Train, Long>,
        JpaSpecificationExecutor<Train> {

    Optional<Train> findByTrainName(String trainName);
}
