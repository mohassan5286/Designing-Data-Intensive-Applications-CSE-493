package com.example.movieinfoservice;

import Repository.RatingsRepository;
import com.example.movieinfoservice.models.Ratings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@ComponentScan({"server"})
@EnableJpaRepositories("Repository")
public class MovieInfoServiceApplication {

    private final int TIMEOUT = 3000000;   // 3 seconds

    @Bean
    public RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(TIMEOUT);   // Set the timeout to 3 seconds
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MovieInfoServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RatingsRepository movieRepository) {
        return args -> {
//            movieRepository.findAll().forEach(s -> {
//                System.out.println(s.getId().getMovieId());
//                System.out.println(s.getId().getUserId());
//                System.out.println(s.getRating());
//            });
            movieRepository.getAvgRatings().forEach(s -> {
                System.out.println(s[0]);
                System.out.println(s[1]);
//                System.out.println(s.getMovieId());
//                System.out.println(s.getAvgRating());
            });
        };
    }
}
