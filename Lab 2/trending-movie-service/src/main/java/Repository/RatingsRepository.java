package Repository;

import com.example.movieinfoservice.models.Rating;
import com.example.movieinfoservice.models.RatingId;
import com.example.movieinfoservice.models.Ratings;
import io.grpc.examples.helloworld.Top10MoviesReply.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, RatingId> {
//public interface RatingsRepository extends JpaRepository<Ratings, String> {
    @Query("SELECT r.id.movie_id, CEILING(AVG(r.rating)) FROM Ratings r GROUP BY r.id.movie_id")
    List<Object[]> getAvgRatings();
    @Query("SELECT r FROM Ratings r")
    List<Ratings> findAllRatings();
}
