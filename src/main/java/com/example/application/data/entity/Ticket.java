package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket extends AbstractEntity {

    @Column(name="userid")
    private String a_userID;

    @Column(name="trainid")
    private String b_trainID;

    @Column(name="date")
    private String c_date;

    public String getUserID() {
        return a_userID;
    }

    public void setUserID(String userID) {
        this.a_userID = userID;
    }

    public String getTrainID() {
        return b_trainID;
    }

    public void setTrainID(String trainID) {
        this.b_trainID = trainID;
    }

    public String getDate() {
        return c_date;
    }

    public void setDate(String date) {
        this.c_date = date;
    }
}
