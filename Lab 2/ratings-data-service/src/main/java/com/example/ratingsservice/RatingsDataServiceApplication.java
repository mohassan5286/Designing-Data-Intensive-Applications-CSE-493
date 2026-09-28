package com.example.ratingsservice;

import com.example.ratingsservice.repository.RatingsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class RatingsDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingsDataServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RatingsRepository movieRepository) {
        return args -> {
//            movieRepository.findAll().forEach(s -> {
//                System.out.println(s.getId().getMovieId());
//                System.out.println(s.getId().getUserId());
//                System.out.println(s.getRating());
//            });
//            movieRepository.findByUserId("U006").forEach(s -> {
//                System.out.println(s);
//            });
            System.out.println(movieRepository.findByUserIdC("U006"));
        };
    }

}
