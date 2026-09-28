from pyspark.sql import SparkSession
import time

# Initialize Spark Session
spark = SparkSession.builder \
    .appName("Aggregation Query from Parquet Files") \
    .master("local[*]") \
    .getOrCreate()

# Define Parquet file paths
# fact_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/FactTableParquets/000cafb0-2c0c-43bb-895e-285e00b21ec1.parquet"
fact_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/FactTableParquets/"
nation_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/NationDimensionTableParquets/"
# nation_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/NationDimensionTableParquets/1075c863-27be-46cb-a31f-21e79670e6f7.parquet"
# supplier_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/SupplierDimensionTableParquets/4f5daddd-532e-4026-83eb-2b4e574380ab.parquet"
supplier_table_path = "/mnt/01D8D4FB872972F0/Life/collage/collage_labs/year_3/term2/DDIA/data/SupplierDimensionTableParquets/"

# Load Parquet files as DataFrames
fact_table = spark.read.parquet(fact_table_path)
nation_table = spark.read.parquet(nation_table_path)
supplier_table = spark.read.parquet(supplier_table_path)

# Show data
fact_table.show()
time.sleep(1)
nation_table.show()
time.sleep(1)
supplier_table.show()
time.sleep(1)

# Register DataFrames as SQL views
fact_table.createOrReplaceTempView("Fact_table")
nation_table.createOrReplaceTempView("Nation")
supplier_table.createOrReplaceTempView("Supplier")

# Execute Query using Spark SQL
query = """
    SELECT 
        n.n_name, 
        s.s_name, 
        SUM(f.l_quantity) AS sum_qty, 
        SUM(f.l_extendedprice) AS sum_base_price, 
        SUM(f.l_extendedprice * (1 - f.l_discount)) AS sum_disc_price, 
        SUM(f.l_extendedprice * (1 - f.l_discount) * (1 + f.l_tax)) AS sum_charge, 
        AVG(f.l_quantity) AS avg_qty, 
        AVG(f.l_extendedprice) AS avg_price, 
        AVG(f.l_discount) AS avg_disc, 
        COUNT(*) AS count_order 
    FROM Fact_table f 
    JOIN Nation n ON f.n_nationkey = n.n_nationkey 
    JOIN Supplier s ON f.s_suppkey = s.s_suppkey 
    GROUP BY n.n_name, s.s_name
"""

# Measure Execution Time
start_time = time.time()
result = spark.sql(query)
execution_time = time.time() - start_time

print(f"Query Execution Time: {execution_time:.4f} seconds")

# Show Full Results
result.show()

# Stop Spark Session
spark.stop()