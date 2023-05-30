package com.example.application.data.service;

import com.example.application.data.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TrainRepository extends
        JpaRepository<Train, Long>,
        JpaSpecificationExecutor<Train> {

    Optional<Train> findByTrainName(String trainName);
    @Query(value = "select * from \"get_trains_for_stations\"(?1, ?2)", nativeQuery = true)
    List<Train> trainList(String from, String to);
    @Query(value = "select * from \"import_csv_data2\"(?1)", nativeQuery = true)
    void uploadTrain(String file);

    @Query(value = "select * from \"get_trains_to_reset\"()", nativeQuery = true)
    void resetTrains();
}
