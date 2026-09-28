package com.moviecatalogservice.services;

import com.moviecatalogservice.models.MovieSummary;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.Top10MoviesReply.Movie;
import io.grpc.examples.helloworld.Top10MoviesReply.Top10MoviesServiceGrpc;
import io.grpc.examples.helloworld.Top10MoviesReply.top10moviesRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Top10MoviesService {
    @GrpcClient("top10-movies-service")
    private Top10MoviesServiceGrpc.Top10MoviesServiceBlockingStub top10MoviesServiceBlockingStub;

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;


    public List<com.moviecatalogservice.models.Movie> getTop10Movies() {

//        String target = "localhost:9090";
//        // Create a channel and blocking stub manually
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
//                .usePlaintext()
//                .build();
//        top10MoviesServiceBlockingStub = Top10MoviesServiceGrpc.newBlockingStub(channel);
        // Call the gRPC server
        top10moviesRequest request = top10moviesRequest.newBuilder().build();
        List<Movie> movies1 = top10MoviesServiceBlockingStub.getTop10Movies(request).getTop10MoviesList();


        List<com.moviecatalogservice.models.Movie> movies2 = movies1.stream()
                .map(protoMovie -> new com.moviecatalogservice.models.Movie(
                        protoMovie.getMovieId(),
                        "",
                        ""
                ))
                .collect(Collectors.toList());
    movies2.forEach(s -> System.out.println(s.getMovieId()));
        System.out.println(movies2);
        for (com.moviecatalogservice.models.Movie movie : movies2) {
            final String url = "https://api.themoviedb.org/3/movie/" + movie.getMovieId() + "?api_key=" + apiKey;
            MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);
            movie.setName(movieSummary.getTitle());
            movie.setDescription(movieSummary.getOverview());
        }

        return movies2;
    }
}
