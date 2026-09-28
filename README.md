# Designing-Data-Intensive-Applications-CSE-493

This Repo Contains The Assignments Taken in the CSE-493 Designing Data Intensive Applications Course

---

## Lab 1: ETL Pipeline and Star Schema Migration

This assignment explores the architectural differences between transactional (OLTP) and analytical (OLAP) database systems by migrating normalized data into a denormalized architecture.

* **ETL Pipeline Construction:** We built an automated data pipeline using Apache NiFi to extract data from a MySQL database, transform row-based Avro records into a columnar Apache Parquet format, and load it into a local file system for analytical querying.

* **Schema Optimization & Evaluation:** We redesigned the relational database into a Star Schema featuring a central Lineitem Fact Table and supporting Dimension Tables. Evaluating the new schema with Apache Spark demonstrated a massive performance increase, reducing complex aggregate query times from 81.77 seconds in MySQL to 0.226 seconds.

---

## Lab 2: Movie Rating Microservices

This assignment focuses on building a fault-tolerant, scalable backend architecture for an IMDb-style movie rating application using distributed microservices.

* **Microservice Architecture:** We developed a suite of microservices using Spring Boot, integrated with Spring Cloud Eureka for service discovery and Spring Cloud Hystrix for Circuit Breaker and Bulkhead patterns to ensure system resilience.

* **Polyglot Persistence & Caching:** We combined a MySQL relational database for strict transactional consistency of user ratings with a MongoDB document database to cache movie metadata. This caching layer optimized redundant external API calls, dramatically reducing average latency from 8856 ms to 2 ms under heavy concurrent load.

* **gRPC Integration:** We implemented a specialized Trending Movies Service utilizing gRPC and Protocol Buffers to efficiently calculate and serve the top 10 highest-rated movies on demand.

---

## Lab 3: Cassandra Distributed Consistency

This assignment tackles distributed database behavior under various node failure scenarios by experimenting with Apache Cassandra's tunable consistency and replication mechanisms.

* **Fault Tolerance Testing:** We deployed a 3-node Cassandra cluster via Docker and simulated specific node downtimes to observe the impact on data availability during both read and write operations across different Replication Factors (RF=1, 2, and 3).

* **Consistency Tuning:** We analyzed the trade-offs of using `QUORUM` consistency and observed Cassandra's eventual consistency mechanisms in action. The lab highlighted how lowering the Consistency Level (CL) per request allows operations to succeed even during partial cluster outages, compensating for delayed gossip protocol failure detection.

---

## Lab 4: Message Queue Benchmarking (Kafka vs. JMS)

This assignment evaluates the performance, usability, and integration ecosystems of distributed messaging systems by comparing Apache Kafka against standard Java Message Service (ActiveMQ).

* **Performance Benchmarking:** We configured both environments and conducted stress tests using 1KB message sizes. The results demonstrated Kafka's superior horizontal scalability, achieving a consumer throughput of over 178,000 messages per second, while JMS provided highly reliable, low-latency point-to-point delivery for smaller-scale loads.

* **Ecosystem & Usability:** We contrasted the developer experience of both platforms, noting Kafka's concise API implementation (requiring minimal lines of code) and its robust native integration ecosystem (Kafka Connect) compared to the more verbose, code-heavy setup required by traditional JMS.

---

## Project: Weather Stations Monitor

This project builds a distributed, real-time streaming pipeline in which simulated weather stations publish sensor readings to Apache Kafka for routing and processing.

* **Stream Processing & Routing:** We simulated 10 weather stations, each producing a reading every second with randomized battery status and a ~10% message drop rate. A Kafka Streams job routes readings by humidity, sending values above 70% to a rain topic and the rest to an archive topic, while dropped and invalid messages go to dedicated topics. Payloads are wrapped in an encrypted envelope, and live Open-Meteo API data is integrated as an additional station.

* **Containerized Deployment:** We packaged the stations and central station with Docker and deployed the full system on Kubernetes, including Kafka and Zookeeper StatefulSets. The Kafka CLI was used to manage topics (create, list, delete with partitioning and replication settings) and to validate the pipeline by producing and consuming messages.
