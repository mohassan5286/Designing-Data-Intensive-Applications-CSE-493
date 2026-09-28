package server;

import Repository.RatingsRepository;
import io.grpc.examples.helloworld.Top10MoviesReply.Movie;
import io.grpc.examples.helloworld.Top10MoviesReply.Top10MoviesServiceGrpc;
import io.grpc.examples.helloworld.Top10MoviesReply.top10moviesReply;
import io.grpc.examples.helloworld.Top10MoviesReply.top10moviesRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@GrpcService
public class Top10MoviesService extends Top10MoviesServiceGrpc.Top10MoviesServiceImplBase {
    @Autowired
    RatingsRepository ratingsRepository;
    @Override
    public void getTop10Movies(top10moviesRequest req, StreamObserver<top10moviesReply> responseObserver) {
        ArrayList<Movie> movies = new ArrayList<>();
        ratingsRepository.getAvgRatings().forEach(s -> {
            movies.add(Movie.newBuilder().setMovieId((String) s[0]).setAvgRating( (Integer) s[1] / 1.0).build() );
        });
        top10moviesReply reply = top10moviesReply.newBuilder().addAllTop10Movies(movies).build();
//        top10moviesReply reply = top10moviesReply.newBuilder().addAllTop10Movies(
//                new ArrayList<>(){{
//                    add(Movie.newBuilder().setMovieId("1").setAvgRating(4.5).build());
//                    add(Movie.newBuilder().setMovieId("2").setAvgRating(4.4).build());
//                    add(Movie.newBuilder().setMovieId("3").setAvgRating(4.3).build());
//                    add(Movie.newBuilder().setMovieId("4").setAvgRating(4.2).build());
//                    add(Movie.newBuilder().setMovieId("5").setAvgRating(4.1).build());
//                    add(Movie.newBuilder().setMovieId("6").setAvgRating(4.0).build());
//                    add(Movie.newBuilder().setMovieId("7").setAvgRating(3.9).build());
//                    add(Movie.newBuilder().setMovieId("8").setAvgRating(3.8).build());
//                    add(Movie.newBuilder().setMovieId("9").setAvgRating(3.7).build());
//                    add(Movie.newBuilder().setMovieId("10").setAvgRating(3.6).build());
//                }}
//        ).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
