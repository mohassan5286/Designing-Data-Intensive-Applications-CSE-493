package com.example.ratingsservice.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RatingId implements Serializable {

    @Column(name = "user_id", length = 10)
    private String user_id;

    @Column(name = "movie_id", length = 10)
    private String movie_id;

    public RatingId() {}

    public RatingId(String user_id, String movie_id) {
        this.user_id = user_id;
        this.movie_id = movie_id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getMovieId() {
        return movie_id;
    }

    public void setMovieId(String movie_id) {
        this.movie_id = movie_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingId ratingId = (RatingId) o;
        return Objects.equals(user_id, ratingId.user_id) &&
                Objects.equals(movie_id, ratingId.movie_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, movie_id);
    }
}