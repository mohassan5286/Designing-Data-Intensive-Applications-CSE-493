CREATE DATABASE  TPCH_DATABASE;
USE TPCH_DATABASE ;
use new_schema;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE Region (
	r_regionkey INT PRIMARY KEY,
	r_name VARCHAR(100) ,
	r_comment VARCHAR(255)
);

CREATE TABLE Nation (
	n_nationkey INT PRIMARY KEY ,
	n_name VARCHAR(100) ,
	n_regionkey INT ,
	n_comment VARCHAR(255) 

	-- FOREIGN KEY (n_regionkey) REFERENCES Region(r_regionkey)
);

CREATE TABLE Customer (
	c_custkey INT PRIMARY KEY ,
	c_name VARCHAR(100) ,
	c_address VARCHAR(255) ,
	c_nationkey INT ,
	c_phone VARCHAR(15) ,
	c_acctbal  FLOAT ,
	c_mktsegment VARCHAR(100) ,
	c_comment VARCHAR(255) 

	-- FOREIGN KEY (c_nationkey) REFERENCES Nation(n_nationkey)
);

CREATE TABLE Supplier (
	s_suppkey INT PRIMARY KEY ,
	s_name VARCHAR(100) ,
	s_address VARCHAR(255) ,
	s_nationkey INT ,
	s_phone VARCHAR(15) ,
	s_acctbal  FLOAT ,
	s_comment VARCHAR(255) 

	-- FOREIGN KEY (s_nationkey) REFERENCES Nation(n_nationkey)
);

CREATE TABLE Part (
	p_partkey INT PRIMARY KEY ,
	p_name VARCHAR(100) ,
	p_mfgr VARCHAR(100) ,
	p_brand VARCHAR(100) ,
	p_type VARCHAR(100) ,
	p_Size INT ,
	p_container VARCHAR(100) ,
	P_retailprice FLOAT ,
	p_comment VARCHAR(255)
);

CREATE TABLE Partsupp (
	ps_partkey INT ,
	ps_suppkey INT ,
	ps_availqty INT ,
	ps_supplycost FLOAT ,
	ps_comment VARCHAR(255) 

	-- PRIMARY KEY (ps_partkey , ps_suppkey) ,
	-- FOREIGN KEY (ps_partkey) REFERENCES Part(p_partkey) ,
	-- FOREIGN KEY (ps_suppkey) REFERENCES Supplier(s_suppkey)
);

CREATE TABLE Orders (
	o_orderkey INT PRIMARY KEY ,
	o_custkey INT,
	o_status VARCHAR(1) ,
	o_totalprice FLOAT ,
	o_orderdate DATE ,
	o_orderpriority VARCHAR(15) ,
	o_cleark VARCHAR(100) ,
	o_shippriority VARCHAR(1) ,
	o_comment VARCHAR(255) 

	-- FOREIGN KEY (o_custkey) REFERENCES Customer(c_custkey) 
);

CREATE TABLE Lineitem (
	l_orderkey INT ,
	l_partkey INT ,
	l_suppkey INT ,
	l_linenumber INT ,
	l_quantity INT ,
	l_extendedprice FLOAT ,
	l_discount FLOAT ,
	l_tax FLOAT ,
	l_returnflag VARCHAR(1) ,
	l_linestatus VARCHAR(1) ,
	l_shipdate DATE ,
	l_commitdate DATE ,
	l_receiptdate DATE ,
	l_shipinstruct VARCHAR(100) ,
	l_shipmode VARCHAR(100) ,
	l_comment VARCHAR(255) 

	-- FOREIGN KEY (l_orderkey) REFERENCES Orders(o_orderkey) ,
	-- FOREIGN KEY (l_partkey , l_suppkey) REFERENCES Partsupp(ps_partkey , ps_suppkey) 
);

show index from Lineitem;

SHOW VARIABLES LIKE 'secure_file_priv' ;
GRANT FILE ON *.* TO 'root'@'localhost';
FLUSH PRIVILEGES; 


LOAD DATA INFILE '/var/lib/mysql-files/region.tbl' INTO TABLE Region
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/nation.tbl' INTO TABLE Nation
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/customer.tbl' INTO TABLE Customer 
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/supplier.tbl' INTO TABLE Supplier 
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n';   
            
LOAD DATA INFILE '/var/lib/mysql-files/part.tbl' INTO TABLE Part
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/partsupp.tbl' INTO TABLE Partsupp
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/orders.tbl' INTO TABLE Orders
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/lineitem.tbl' INTO TABLE Lineitem
		FIELDS TERMINATED BY '|' 
		LINES TERMINATED BY '\n'; 
            
SELECT COUNT(*) AS Regions_count FROM Region ;
SELECT COUNT(*) AS Nations_count FROM Nation ;
SELECT COUNT(*) AS Customers_count FROM Customer ;
SELECT COUNT(*) AS Suppliers_count FROM Supplier ;
SELECT COUNT(*) AS Parts_count FROM Part ;
SELECT COUNT(*) AS Parts_Suppliers_count FROM Partsupp ;
SELECT COUNT(*) AS Orders_count FROM Orders ;
SELECT COUNT(*) AS LineItems_count FROM Lineitem ;

select 
	n_name, 
	s_name,
	sum(l_quantity) as sum_qty,
	sum(l_extendedprice) as sum_base_price,
	sum(l_extendedprice * (1 - l_discount)) as sum_disc_price,
	sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge,
	avg(l_quantity) as avg_qty,
	avg(l_extendedprice) as avg_price,
	avg(l_discount) as avg_disc ,
	count(*) as count_order
from
	Lineitem,
	Orders,
	Customer,
	Nation,
	Partsupp,
	Supplier
where
	l_shipdate <= date'1998-12-01' -interval '90' day AND
	l_orderkey = o_orderkey AND
	o_custkey = c_custkey AND 
	c_nationkey = n_nationkey AND
	l_partkey = ps_partkey AND
	l_suppkey = ps_suppkey AND
	ps_suppkey = s_suppkey
group by 
	n_name ,  
	s_name ;

SELECT 
    n.n_name, 
    s.s_name,
    SUM(l.l_quantity) AS sum_qty,
    SUM(l.l_extendedprice) AS sum_base_price,
    SUM(l.l_extendedprice * (1 - l.l_discount)) AS sum_disc_price,
    SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge,
    AVG(l.l_quantity) AS avg_qty,
    AVG(l.l_extendedprice) AS avg_price,
    AVG(l.l_discount) AS avg_disc,
    COUNT(*) AS count_order
FROM Lineitem l
JOIN Orders o ON l.l_orderkey = o.o_orderkey
JOIN Customer c ON o.o_custkey = c.c_custkey
JOIN Nation n ON c.c_nationkey = n.n_nationkey
JOIN Partsupp ps ON l.l_partkey = ps.ps_partkey AND l.l_suppkey = ps.ps_suppkey
JOIN Supplier s ON ps.ps_suppkey = s.s_suppkey
WHERE l.l_shipdate <= '1998-09-02'
GROUP BY n.n_name, s.s_name
ORDER BY n.n_name;

SELECT 
    n.n_name, 
    s.s_name, 
    SUM(l.l_quantity) AS sum_qty, 
    SUM(l.l_extendedprice) AS sum_base_price, 
    SUM(l.l_extendedprice * (1 - l.l_discount)) AS sum_disc_price, 
    SUM(l.l_extendedprice * (1 - l.l_discount) * (1 + l.l_tax)) AS sum_charge, 
    AVG(l.l_quantity) AS avg_qty, 
    AVG(l.l_extendedprice) AS avg_price, 
    AVG(l.l_discount) AS avg_disc, 
    COUNT(*) AS count_order
FROM Lineitem l
JOIN Orders o ON l.l_orderkey = o.o_orderkey
JOIN Customer c ON o.o_custkey = c.c_custkey
JOIN Nation n ON c.c_nationkey = n.n_nationkey
JOIN Supplier s ON l.l_suppkey = s.s_suppkey
WHERE l.l_shipdate <= '1998-09-02'
GROUP BY n.n_name, s.s_name
ORDER BY n.n_name;  
