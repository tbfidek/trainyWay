package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket extends AbstractEntity {

    @Column(name="userid")
    private Long a_userID;

    @Column(name="trainid")
    private Long b_trainID;

    @Column(name="date")
    private Date c_date;

    @Column(name="departure_station")
    private String dep_station;

    @Column(name="arrival_station")
    private String arr_station;

    @Column(name="wagon_number")
    private String wagon_number;

    @Column(name="seat_number")
    private String seat_number;


    public String getDep_station() {
        return dep_station;
    }

    public void setDep_station(String dep_station) {
        this.dep_station = dep_station;
    }

    public String getArr_station() {
        return arr_station;
    }

    public void setArr_station(String arr_station) {
        this.arr_station = arr_station;
    }

    public String getWagon_number() {
        return wagon_number;
    }

    public void setWagon_number(String wagon_number) {
        this.wagon_number = wagon_number;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public Long getUserID() {
        return a_userID;
    }

    public void setUserID(Long userID) {
        this.a_userID = userID;
    }

    public Long getTrainID() {
        return b_trainID;
    }

    public void setTrainID(Long trainID) {
        this.b_trainID = trainID;
    }

    public Date getDate() {
        return c_date;
    }

    public void setDate(Date date) {
        this.c_date = date;
    }
}
