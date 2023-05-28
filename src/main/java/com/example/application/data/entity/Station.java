package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.TimeZoneColumn;

import java.sql.Time;


@Entity
@Table(name="stations")
public class Station extends AbstractEntity {

    @Column(name="train_id")
    private Integer trainID;

    @Column(name="station_name")
    private String stationName;

    @Column(name="arrival_time")
    private Integer arrTime;

    @Column(name="departure_time")
    private Integer depTime;

    @Column(name="stationary_time")
    private Integer stationary_time;

    public Integer getStationaryTime() {
        return stationary_time;
    }

    public void setStationaryTime(Integer stationary_time) {
        this.stationary_time = stationary_time;
    }

    public Integer getTrainID() {
        return trainID;
    }

    public void setTrainID(Integer trainID) {
        this.trainID = trainID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getArrTime() {
        return arrTime;
    }

    public void setArrTime(Integer arrTime) {
        this.arrTime = arrTime;
    }

    public Integer getDepTime() {
        return depTime;
    }

    public void setDepTime(Integer depTime) {
        this.depTime = depTime;
    }
}