package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.TimeZoneColumn;

import java.sql.Time;


@Entity
@Table(name="stations")
public class Station extends AbstractEntity {

    private String trainID;

    private String stationName;

    private Time arrTime;
    
    private Time depTime;

    public String getTrainID() {
        return trainID;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Time getArrTime() {
        return arrTime;
    }

    public void setArrTime(Time arrTime) {
        this.arrTime = arrTime;
    }

    public Time getDepTime() {
        return depTime;
    }

    public void setDepTime(Time depTime) {
        this.depTime = depTime;
    }
}