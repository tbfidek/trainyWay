package com.example.application.data.service;

import com.example.application.data.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository repository;
    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }
    public Optional<Ticket> get(Long id) {
        return repository.findById(id);
    }
    public String[] getAvailableSeats(Long trainId, Date date, String wagonNumber) { return repository.getAvailableSeats(trainId, date, wagonNumber).split(","); }
    public float getTicketPrice(Long trainID, String start_station, String end_station, Integer wagon_type) { return repository.getTicketPrice(trainID, start_station, end_station, wagon_type);}
    public List<Ticket> getAll() { return repository.findAll(); }
    public Ticket update(Ticket entity) {
        return repository.save(entity);
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
    public Page<Ticket> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Page<Ticket> list(Pageable pageable, Specification<Ticket> filter) {
        return repository.findAll(filter, pageable);
    }
    public int count() {
        return (int) repository.count();
    }

}