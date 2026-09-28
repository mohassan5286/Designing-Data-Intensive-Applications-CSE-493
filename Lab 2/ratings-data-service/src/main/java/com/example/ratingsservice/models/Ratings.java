package com.example.ratingsservice.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ratings")
public class Ratings {

    @EmbeddedId
    private RatingId ratingId;

    @Column(nullable = false)
    private int rating;

    public Ratings() {}

    public Ratings(RatingId id, int rating) {
        this.ratingId = id;
        setRating(rating);
    }

    public RatingId getId() {
        return ratingId;
    }

    public void setId(RatingId id) {
        this.ratingId = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 1 and 10");
        }
        this.rating = rating;
    }
}
