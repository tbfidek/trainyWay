package com.example.application.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train extends AbstractEntity{
    @Column(name="train_name")
    private String trainName;
    @Column(name="departure_station")
    private String depStation;
    @Column(name="departure_time")
    private Integer depTime;
    @Column(name="arrival_station")
    private String arrStation;
    @Column(name="arrival_time")
    private Integer arrTime;
    @Column(name="seats")
    private Integer seats;
    @Column(name="delay")
    private Integer delay;
    @Column(name="rating")
    private float rating;
    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }
    public Integer getSeats() {
        return seats;
    }
    public void setSeats(Integer seats) {
        this.seats = seats;
    }
    public String getTrainName() {
        return trainName;
    }
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
    public String getDepStation() {
        return depStation;
    }
    public void setDepStation(String depStation) {
        this.depStation = depStation;
    }
    public Integer getDepTime() {
        return depTime;
    }
    public void setDepTime(Integer depTime) {
        this.depTime = depTime;
    }
    public String getArrStation() {
        return arrStation;
    }
    public void setArrStation(String arrStation) {
        this.arrStation = arrStation;
    }
    public Integer getArrTime() {
        return arrTime;
    }
    public void setArrTime(Integer arrTime) {
        this.arrTime = arrTime;
    }
    public Integer getDelay() {
        return delay;
    }
    public void setDelay(Integer delay) {
        this.delay = delay;
    }
}
