package com.example.application.data.service;

import com.example.application.data.entity.Station;
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

    @Query(value = "select * from \"getstationsafter\"(?1, ?2)", nativeQuery = true)
    List<Station> getAfterStations(Long id, String stationName);
    @Query(value = "select * from \"search_from_to\"()", nativeQuery = true)
    List<String> stationList();
    @Query(value = "select * from \"get_arrivals_from\"(?1)", nativeQuery = true)
    String arrivalsList(String stationName);
    @Query(value = "select * from \"get_departures_from\"(?1)", nativeQuery = true)
    String departuresList(String stationName);
}

