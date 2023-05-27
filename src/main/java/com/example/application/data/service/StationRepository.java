package com.example.application.data.service;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends
        JpaRepository<Station, Long>,
        JpaSpecificationExecutor<Station> {
    @Query(value = "select * from \"getstationsbytrainid\"(?1)", nativeQuery = true)
    List<Station> getStationsByTrainId(String id);

    Optional<Station> findByStationName(String stationName);

    @Query(value = "select * from \"getafterstations\"(?1, ?2)", nativeQuery = true)
    List<Station> getAfterStations(Long id, String stationName);
}

