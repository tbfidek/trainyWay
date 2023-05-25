package com.example.application.data.service;

import com.example.application.data.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StationRepository extends
        JpaRepository<Station, Long>,
        JpaSpecificationExecutor<Station> {
    @Query(value = "select * from stations where trainid = ?1", nativeQuery = true)
    List<Station> stationList(String id);

}

