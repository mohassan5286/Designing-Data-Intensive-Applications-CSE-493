package com.example.ratingsservice.repository;

import com.example.ratingsservice.models.Rating;
import com.example.ratingsservice.models.RatingId;
import com.example.ratingsservice.models.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, RatingId> {
    //public interface RatingsRepository extends JpaRepository<Ratings, String> {
    @Query("SELECT r.id.movie_id, CEILING(AVG(r.rating)) FROM Ratings r GROUP BY r.id.movie_id")
    List<Object[]> getAvgRatings();
    @Query("SELECT r FROM Ratings r")
    List<Ratings> findAllRatings();

    @Query("SELECT r FROM Ratings r where r.id.user_id = :userId")
    List<Ratings> findByUserIdC(@Param("userId") String userId);

    Optional<Ratings> findByRatingId(RatingId ratingId);

}


