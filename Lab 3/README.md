## Assignment Overview & Objective

This assignment explores the behavior of distributed database systems under various failure scenarios. The primary objective is to experiment with Apache Cassandra's tunable consistency levels and replication factors. This is achieved by setting up a 3-node cluster managed via Docker to simulate different node downtime situations and observe their impact on data availability during read and write operations.

## Lab Setup

* **Environment:** A Docker-compose application running a cluster of 3 Cassandra nodes (Cassandra1-1, Cassandra2-1, and Cassandra-seed-1).
* **Operations:** Executed CQL commands (`cqlsh`) to create a `bookstore` keyspace, insert records, and query data under simulated node downtime scenarios (`docker container pause`).

## Experiment Results

### 1. Read Operations (SELECT)

The table below illustrates the success or failure of read operations under different replication factors and node availability conditions.

| Nodes Down | RF=1 | RF=2 (QUORUM) | RF=3 (QUORUM) |
| --- | --- | --- | --- |
| Node 1 | Failure | Failure | Success |
| Node 2 | Success | Success | Success |
| Seed (Node 3) | Success | Failure | Success |
| Node 1 & 2 | Failure | Failure | Failure |
| Node 1 & Seed | Failure | Failure | Failure |
| Node 2 & Seed | Success | Failure | Failure |

### 2. Write Operations (INSERT)

The table below illustrates the success or failure of write operations across the same downtime scenarios.

| Nodes Down | RF=1 | RF=2 (QUORUM) | RF=3 (QUORUM) |
| --- | --- | --- | --- |
| Node 1 | Error | Error | OK |
| Node 2 | OK | OK | OK |
| Seed (Node 3) | OK | Error | OK |
| Node 1 & 2 | Error | Error | Error |
| Node 1 & Seed | Error | Error | Error |
| Node 2 & Seed | OK | Error | Error |

## Key Observations & Consistency Tuning

* **RF=1:** Data was stored entirely on Node 1. Read and write queries consistently failed whenever this specific node was down.
* **RF=2 (QUORUM):** A `QUORUM` requires a majority of replicas to be available (at least 2 out of 3 nodes). Operations failed if more than one node was down or if critical replica nodes were offline.
* **RF=3 (QUORUM):** Provided the highest fault tolerance, allowing operations to succeed during any single-node failure.
* **Tuning for Availability:** In scenarios where operations failed due to node outages, availability could be restored by lowering the per-request Consistency Level (CL) to `ONE` (for reads and writes) or `ANY` (for writes), allowing queries to succeed as long as at least one replica or active node was reachable.

## Questions and Discussion

During the execution of the lab experiments, a few unexpected behaviors were observed and analyzed:

**1. Discrepancy in Active Replicas Count**

* **Observation:** During an RF=2 configuration, when both Node 1 and the Seed node (Node 3) were down, the active replicas were expected to be 0, but the response indicated 1 active replica.


* **Discussion:** This occurs due to Cassandra's eventually consistent failure detection mechanism. Cassandra uses a gossip protocol to detect node failures. Because nodes learn about failures at different times, the coordinator node might only report one unavailable replica if the other has not yet been fully detected as down across the cluster. Additionally, if hinted handoff is enabled, Cassandra may temporarily assume a down replica can still receive writes later.



**2. Insertions Exceeding Successful Queries**

* **Observation:** The insert query was reported to succeed only one time, yet when selecting data from the table, 4 rows were found to be inserted.


* **Discussion:** In Cassandra, if a write operation fails to meet the requested Consistency Level (and thus returns an error to the client), the data may still be written to the commit log of the nodes that are currently available. Furthermore, hints may be stored locally to be replayed to the offline nodes once they come back online, resulting in the data eventually persisting despite the initial query failure.
