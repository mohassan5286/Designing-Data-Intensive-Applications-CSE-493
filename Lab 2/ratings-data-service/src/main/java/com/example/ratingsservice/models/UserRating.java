package com.example.ratingsservice.models;

import lombok.Getter;

import java.util.List;

@Getter
public class UserRating {
    private List<Rating> ratings;

    public UserRating() {
    }

    public UserRating(List<Rating> ratings) {
        this.ratings = ratings;
    }



    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
