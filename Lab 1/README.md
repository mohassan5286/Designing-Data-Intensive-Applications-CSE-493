## 1. Assignment Overview & Objective

Relational Entity-Relationship Diagrams (ERDs) are frequently optimized for Online Transactional Processing (OLTP) to minimize data redundancy and handle transactional writes efficiently. However, these normalized schemas are not ideal for Online Analytical Processing (OLAP), which requires complex aggregations and groupings across massive datasets.  

The primary objective of this assignment is to perform an Extract, Transform, and Load (ETL) process to migrate a normalized OLTP relational database schema into a denormalized Star Schema, which is significantly more optimized for analytical queries.  

## 2. Data Preparation: Generation & Insertion

The initial phase involved simulating a transactional database environment:

*   **Source Data Generation:** The standardized `tpch-dbgen` utility was cloned from GitHub to generate the TPCH dataset.  
*   **Scale Factor:** The data was generated with a scale factor of 1 (`-s 1`), resulting in a 1GB dataset of `.tbl` files.  
*   **MySQL Loading:** The generated data (including customer, lineitem, orders, part, partsupp, supplier, nation, and region tables) was moved to the `/var/lib/mysql-files/` directory and inserted into a MySQL database to represent the source OLTP system.  

## 3. The ETL Pipeline (Apache NiFi)

To transition the data from an operational state to an analytical state, a data pipeline was constructed using Apache NiFi. The ETL graph consists of three primary transformation stages:  

*   **Data Extraction (ExecuteSQL):** This processor connects to the MySQL database, executes SQL queries to extract the necessary table joins, and outputs the result as Avro data.  
*   **Format Transformation (ConvertAvroToParquet):** This processor takes the row-based Avro data and converts it into Apache Parquet. Parquet is a columnar storage format, which is a critical requirement for accelerating OLAP queries.  
*   **Data Loading (PutFile):** The final processor saves the newly structured Parquet files to a specified directory on the local file system for querying.  

## 4. Database Design: Star Schema Transformation

Instead of porting all MySQL tables, the schema was heavily reduced to serve a specific, complex analytical query joining six tables.  

*   **Omitted Tables:** The Region, Partsupp, and Part tables were excluded from the pipeline as they were not required for the specific analytical workload.  
*   **Fact Table:** A central Lineitem Fact Table was created by joining the original Lineitem, Orders, and Customer tables.  
*   **Dimension Tables:** Supplier and Nation were established as Dimension tables, referenced by the Fact Table via SUPPKEY and CUST_NATIONKEY respectively.  

## 5. Performance Evaluation: Spark vs. MySQL

To evaluate the effectiveness of the Star Schema and columnar storage, an intensive query calculating sums, averages, and discounts was executed 8 separate times on both the original MySQL database and the new Parquet files via Apache Spark.  

### Benchmark Results

| Metric | MySQL (OLTP / Row-Based) | Apache Spark (OLAP / Columnar) |
| :--- | :--- | :--- |
| **Average Execution Time** | 81.77 seconds | 0.226 seconds |
| **Fastest Run** | 76.05 seconds | 0.163 seconds |
| **Slowest Run** | 96.33 seconds | 0.321 seconds |

### Results Analysis

The benchmark results highlight a massive performance disparity, perfectly illustrating the architectural differences between transactional and analytical systems:

*   **Architecture Limits:** MySQL is fundamentally designed to insert and update single rows quickly (OLTP). When forced to scan a 6,000,000-row `lineitem` table and perform 6-way joins for aggregations, it bottlenecks heavily on disk I/O, resulting in an 81-second average wait time.  
*   **Columnar Efficiency:** Spark queries the data in a fraction of a second because it is reading from the converted Parquet files. Parquet's columnar nature allows the Spark engine to completely ignore irrelevant data and only scan the specific columns required for the math.
*   **Measurement Scope:** It is important to note that the 0.226 second Spark average represents the isolated execution time of the query itself. This metric intentionally excludes the time taken by NiFi to perform the ETL transformation and the initialization time required to boot the Spark cluster, focusing purely on the engine's query resolution speed on prepared analytical data.