package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "trains")
public class Train extends AbstractEntity{

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getBlabla() {
        return blabla;
    }

    public void setBlabla(String blabla) {
        this.blabla = blabla;
    }

    private String trainName;
    private String blabla;



}
