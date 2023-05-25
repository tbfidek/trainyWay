package com.example.application.data.service;

import com.example.application.data.entity.Ticket;
import com.example.application.data.entity.Train;
import com.example.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TicketRepository extends
        JpaRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {

    @Query(value = "SELECT * from \"test1\"()", nativeQuery = true)
    public List<Ticket> test();
}
