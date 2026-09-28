## Assignment Overview & Objective

This assignment presents a performance benchmark and comparative analysis of Apache Kafka and Java Message Service (JMS) via ActiveMQ. Both message queuing systems are evaluated based on throughput, latency, usability, and integration capabilities using a standardized 1KB message size within a Java environment.  

## Environment Setup

* **Apache Kafka:** Configured via a 2-step Docker setup (`docker-compose.yml` with Zookeeper) and Spring Boot.


* **JMS (ActiveMQ):** Configured manually via local binary execution and Maven dependencies.

---

## 1. Performance Benchmarks

Here is the corrected and properly formatted Markdown table. The issue was caused by unintended line breaks and invisible whitespace characters inside the rows, which breaks standard Markdown table rendering.

| Metric | Apache Kafka | JMS (ActiveMQ) |
| --- | --- | --- |
| **Producer Throughput** | 147,605 records/sec (140.77 MB/s) | N/A (Did not fail under load testing) |
| **Consumer Throughput** | 178,562 messages/sec (170.2 MB/s) | N/A |
| **Producer Response Time** | N/A | Median: 2 ms (Avg: 2.42 ms) |
| **Consumer Response Time** | N/A | Median: 0 ms (Avg: 0.231 ms) |
| **Median Latency** | ~2.58 ms to 2.70 ms | 0 ms (Avg: 7.271 ms) |

---

## 2. Usability

* **Apache Kafka:** Highly concise. Creating a producer/consumer requires only ~3 to 20 lines of code and minimal API calls (2-4).


* **JMS:** Highly verbose and prone to code clutter. Requires ~35 to 40 lines of code, 7 sequential API calls per operation, and manual resource cleanup protocols. Initial setup time was significantly longer (~1.5 hours).
---

## 3. Integrations Ecosystem

* **Apache Kafka:** Natively built for diverse data pipelines. The Kafka Connect interface provides hundreds of out-of-the-box connectors for systems like PostgreSQL, Elasticsearch, Hadoop HDFS, and Cassandra without custom coding.


* **JMS:** Restricted to Java environments as a strict messaging layer. Integrating with external storage systems requires writing custom intermediary code to consume, parse, and sink the data.



---

## Conclusion & Recommendation

* **Choose Apache Kafka** for modern, large-scale, distributed architectures that require massive horizontal scalability, fault tolerance, high throughput, and seamless data streaming integrations.


* **Choose JMS** for simpler, smaller-scale traditional enterprise applications where strict point-to-point reliability is needed, and horizontal scalability is not a primary concern.