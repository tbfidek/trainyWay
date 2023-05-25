package com.example.application.data.service;

import com.example.application.data.entity.Ticket;
import com.example.application.data.entity.Train;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public List<Ticket> test() { return repository.test();}

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