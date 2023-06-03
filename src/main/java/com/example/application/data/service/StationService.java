package com.example.application.data.service;

import com.example.application.data.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StationService {
    private final StationRepository repository;
    public StationService(StationRepository repository) {
        this.repository = repository;
    }
    public Optional<Station> get(Long id) {
        return repository.findById(id);
    }
    public Optional<Station> get(String stationName) {
        return repository.findByStationName(stationName);
    }
    public List<Station> getAll() {
        return repository.findAll();
    }
    public Station update(Station entity) {
        return repository.save(entity);
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
    public Page<Station> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Page<Station> list(Pageable pageable, Specification<Station> filter) {
        return repository.findAll(filter, pageable);
    }
    public int count() {
        return (int) repository.count();
    }
    public List<Station> getStationsByTrainId(String id) {
        return repository.getStationsByTrainId(id);
    }
    public List<Station> getAfterStations(Long id, String stationName) {
        return repository.getAfterStations(id, stationName);
    }
    public List<String> stationList() {
        return repository.stationList();
    }
    public List<List<String>> arrivalsList(String stationName) {
        List<List<String>> arrivals = new ArrayList<>();
        for (String s : repository.arrivalsList(stationName).split("\\),")) {
            arrivals.add(List.of(s.replace("(", "").replace(")", "").split(",")));
        }
        return arrivals;
    }
    public List<List<String>> departuresList(String stationName) {
        List<List<String>> departures = new ArrayList<>();
        for (String s : repository.departuresList(stationName).split("\\),")) {
            departures.add(List.of(s.replace("(", "").replace(")", "").split(",")));
        }
        return departures;

    }

}