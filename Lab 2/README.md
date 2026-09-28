# Assignment Overview & Objective

In this assignment, multiple microservices communicate with each other to provide the backend services of a minimalistic/sample movie rating application similar to IMDb.

## Technologies

- Spring Boot
- Spring Cloud Eureka (Service Discovery)
- Spring Cloud Hystrix (Dashboard, Circuit Breaker pattern, Bulkhead pattern)
- MySQL (Ratings Persistence Layer)
- MongoDB (Movie Metadata Caching Layer)
- gRPC & Protocol Buffers (Trending Movie Analytics Service)

---

## Summary

- **MovieInfoService** provides the movie info by sending requests to **TheMovieDB API**. It utilizes a MongoDB cache layer to optimize redundant external calls.
- **RatingsDataService** provides the user's ratings for movies, backed by a persistent MySQL relational database.
- **MovieCatalogService** acts as an accumulator that gets data from **RatingsDataService** and **MovieInfoService** to present it.
- **TrendingMoviesService** is a gRPC service that calculates the top 10 highest-rated trending movies on demand.
- **DiscoveryServer** is the Eureka server for service discovery.

---

## Running the Application

You can run each project either using your IDE or `mvn spring-boot:run` starting from the **DiscoveryServer**. Alternatively, you can boot all services simultaneously using the provided batch script:

```cmd
scripts\start_all_services.bat
```

### Endpoints

- **Discovery Server** - Port `8761`
- **Movie Catalog** - `/catalog/{userId}` (Port `8081`)
- **Trending Movies (via Catalog REST)** - `/catalog/top10movies` (Port `8081`)
- **Trending Movies (gRPC Protocol)** - Port `500`
- **Movie Info** - `/movies/{movieId}` (Port `8082`)
- **Ratings Data** - `/{userId}/{movieId}` (Port `8083`)
- **Hystrix Dashboard** - Go to `/hystrix` on Port `8081`. Then enter `/actuator/hystrix.stream` into the input box.

---

## Database Schemas

### MySQL Schema (`ratings-data-service`)

```sql
CREATE TABLE ratings (
    user_id VARCHAR(10),  
    movie_id VARCHAR(10), 
    rating INT CHECK (rating BETWEEN 1 AND 10), 
    PRIMARY KEY (user_id, movie_id) 
);
```

### MongoDB Document Schema (`movie-info-service`)

```json
{
  "collection": "movies",
  "fields": {
    "_id": {
      "type": "ObjectId",
      "description": "Unique identifier for the document"
    },
    "movieId": {
      "type": "string",
      "description": "Unique identifier for the movie",
      "required": true
    },
    "name": {
      "type": "string",
      "description": "Name of the movie",
      "required": true
    },
    "description": {
      "type": "string",
      "description": "Description of the movie",
      "required": false
    }
  },
  "indexes": [
    {
      "keys": { "movieId": 1 },
      "options": { "unique": true }
    }
  ]
}
```

---

## Performance & Stress Testing (Apache JMeter)

### 1. Proof of Caching Benefit

*Configuration: 1,000 Concurrent Threads, Loop Count = 100, Ramp-Up = 100s (100,000 Total Requests)*

| Metric | Before Caching (External API) | After Caching (MongoDB Layer) |
| --- | --- | --- |
| **Average Latency** | 8856 msec | **2 msec** |
| **Median Latency** | 5605 msec | **1 msec** |
| **P90 Latency** | 19669 msec | **6 msec** |
| **Throughput** | 81 request/sec | **1000 request/sec** |

### 2. High-Concurrency Performance Test

*Configuration: 3000 Concurrent Threads, Ramp-Up Time = 1 sec, Loop Count = 1*

- **Before Caching Baseline:**
  - **90% Line:** 5.5 sec
  - **95% Line:** 5.9 sec
  - **99% Line:** 6.4 sec
  - **Throughput:** 415.1 request/sec

- **After Cache Optimization:**
  - **P90 Latency:** 1321 msec
  - **Throughput:** 930 request/sec

### 3. Maximum System Load Capacity (Stress Thresholds)

| Scenario | Max Requests Fulfilled Without Failure |
| --- | --- |
| **Before Mongo Cache** | **200 requests** |
| **After Mongo Cache** | **4000 requests** (breaks at 5000) |
| **MySQL Service Layer** | **7000 requests** (breaks at 8000) |

---

## Technical Discussion

### 1. Why did we recommend a relational DB (MySQL) for the Ratings Service?

- **Consistency & Reliability:** Ratings require strict transactional integrity (ACID properties) to prevent race conditions during updates.
- **Relational Integrity:** Naturally models relational connections between users, movies, and numerical scores.
- **Indexed Execution:** Fast lookup performance via composite primary key indexing on `(user_id, movie_id)`.

### 2. Why did we suggest caching in the MovieDB Service, not other services?

- **High Read-to-Write Ratio:** Movie details and descriptions rarely change after release, making metadata ideal for caching.
- **Latency Bottleneck Elimination:** Third-party API calls over the network are resource-intensive and introduce significant response delays.
- **Why not other services?**
  - *Ratings Service:* User ratings change frequently, creating high cache invalidation overhead.
  - *User Service:* User data contains sensitive, dynamic state requiring real-time freshness.

### 3. From which data source does the Trending Movies Service fetch its data? Is it adequate? How can it be improved?

- **Data Source:** Fetches raw rating records directly from the MySQL database (`RatingsDataService`).
- **Adequacy:** 
  - Adequate if ranking aggregations are precomputed.
  - Inadequate if full-table sorting and grouping are executed on demand across millions of records per request.
- **Improvement Strategies:**
  - **Precompute Rankings:** Execute an asynchronous background scheduler (e.g., cron job) to recalculate top rankings periodically.
  - **In-Memory Storage:** Persist precomputed top-10 lists inside Redis or MongoDB for fast read performance.
  - **Index Optimization:** Add composite covering indexes on `(movie_id, rating)` in MySQL to minimize query execution overhead.