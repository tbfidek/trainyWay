package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review extends AbstractEntity {
    @Column(name="user_id")
    private Long userID;
    @Column(name="train_id")
    private Long trainID;
    @Column(name="rating")
    private float rating;
    public Review(Long user_id, Long train_id, float rating){
        this.userID = user_id;
        this.trainID = train_id;
        this.rating = rating;

    }

    public Review() {
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getTrainID() {
        return trainID;
    }

    public void setTrainID(Long trainID) {
        this.trainID = trainID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
