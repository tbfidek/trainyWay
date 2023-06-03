package com.example.application.data.service;

import com.example.application.data.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface TicketRepository extends
        JpaRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {

    @Query(value = "SELECT get_ticket_price from \"get_ticket_price\"(?1, ?2, ?3, ?4)", nativeQuery = true)
    public float getTicketPrice(Long trainID, String start_station, String end_station, Integer wagon_type);

    @Query(value = "SELECT * FROM \"get_available_seats\"(?1, ?2, ?3)", nativeQuery = true)
    public String getAvailableSeats(Long trainId, Date date, String wagonNumber);
}
