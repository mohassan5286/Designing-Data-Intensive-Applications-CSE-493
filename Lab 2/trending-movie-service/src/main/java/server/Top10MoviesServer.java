package server;



/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Mine

import Repository.RatingsRepository;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
//import io.grpc.examples.helloworld.GreeterGrpc;
//import io.grpc.examples.helloworld.HelloReply;
//import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.Top10MoviesReply.Movie;
import io.grpc.examples.helloworld.Top10MoviesReply.Top10MoviesServiceGrpc;
import io.grpc.examples.helloworld.Top10MoviesReply.top10moviesRequest;
import io.grpc.examples.helloworld.Top10MoviesReply.top10moviesReply;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.annotations.GrpcGenerated;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class Top10MoviesServer {
    private static final Logger logger = Logger.getLogger(Top10MoviesServer.class.getName());
    @Autowired
    private static RatingsRepository movieRepository;
    private Server server;
    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        /*
         * By default gRPC uses a global, shared Executor.newCachedThreadPool() for gRPC callbacks into
         * your application. This is convenient, but can cause an excessive number of threads to be
         * created if there are many RPCs. It is often better to limit the number of threads your
         * application uses for processing and let RPCs queue when the CPU is saturated.
         * The appropriate number of threads varies heavily between applications.
         * Async application code generally does not need more threads than CPU cores.
         */
        ExecutorService executor = Executors.newFixedThreadPool(2);
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .executor(executor)
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Top10MoviesServer.this.stop();
                } catch (InterruptedException e) {
                    if (server != null) {
                        server.shutdownNow();
                    }
                    e.printStackTrace(System.err);
                } finally {
                    executor.shutdown();
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final Top10MoviesServer server = new Top10MoviesServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends Top10MoviesServiceGrpc.Top10MoviesServiceImplBase {

        @Override
        public void getTop10Movies(top10moviesRequest req, StreamObserver<top10moviesReply> responseObserver) {
            ArrayList<Movie> movies = new ArrayList<>();
            movieRepository.getAvgRatings().forEach(s -> {
                movies.add(Movie.newBuilder().setMovieId((String) s[0]).setAvgRating((double) s[1]).build());
                    });
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println(movies);
            top10moviesReply reply = top10moviesReply.newBuilder().addAllTop10Movies(movies).build();
//            top10moviesReply reply = top10moviesReply.newBuilder().addAllTop10Movies(
//                    new ArrayList<Movie>(){{
//                        add(Movie.newBuilder().setMovieId("1").setAvgRating(4.5).build());
//                        add(Movie.newBuilder().setMovieId("2").setAvgRating(4.4).build());
//                        add(Movie.newBuilder().setMovieId("3").setAvgRating(4.3).build());
//                        add(Movie.newBuilder().setMovieId("4").setAvgRating(4.2).build());
//                        add(Movie.newBuilder().setMovieId("5").setAvgRating(4.1).build());
//                        add(Movie.newBuilder().setMovieId("6").setAvgRating(4.0).build());
//                        add(Movie.newBuilder().setMovieId("7").setAvgRating(3.9).build());
//                        add(Movie.newBuilder().setMovieId("8").setAvgRating(3.8).build());
//                        add(Movie.newBuilder().setMovieId("9").setAvgRating(3.7).build());
//                        add(Movie.newBuilder().setMovieId("10").setAvgRating(3.6).build());
//                    }}
//            ).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

    }
}

