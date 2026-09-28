package com.example.ratingsservice.resources;

import com.example.ratingsservice.models.RatingId;
import com.example.ratingsservice.models.Ratings;
import com.example.ratingsservice.repository.RatingsRepository;
import com.example.ratingsservice.models.Rating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
@RequestMapping("/ratings")
public class RatingsResource {

    @Autowired
    private RatingsRepository ratingsRepository;

//    @GetMapping("/{userId}")
//    public Rating getRatingsOfUser(@PathVariable String userId) {
//
//        return ratingsRepository.findMovieIdAndRatingByUserId(userId);
//    }
@GetMapping("/{userId}/{movieId}")
public ResponseEntity<Ratings> getRating(@PathVariable String userId, @PathVariable String movieId) {
    RatingId ratingId = new RatingId(userId, movieId);
    Optional<Ratings> rating = ratingsRepository.findByRatingId(ratingId);

    return rating.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}


}

//@RestController
//@RequestMapping("/ratings")
//public class RatingsResource {
//
//    @RequestMapping("/{userId}")
//    public UserRating getRatingsOfUser(@PathVariable String userId) {
//        List<Rating> ratings = Arrays.asList(
//                new Rating("550", 4)
//        );
//
//        return new UserRating(ratings);
//    }
//}
