CREATE DATABASE  TPCH_DATABASE;
USE TPCH_DATABASE ;

CREATE TABLE Region (
	r_regionkey   INTEGER NOT NULL PRIMARY KEY,
	r_name        CHAR(25) NOT NULL,
	r_comment     VARCHAR(152)
);

CREATE TABLE Nation (
	n_nationkey   INTEGER NOT NULL PRIMARY KEY ,
	n_name        CHAR(25) NOT NULL,
	n_regionkey   INTEGER NOT NULL ,
	n_comment     VARCHAR(152) ,

	FOREIGN KEY (n_regionkey) REFERENCES Region(r_regionkey)
);

CREATE TABLE Customer (
	c_custkey      INTEGER NOT NULL PRIMARY KEY ,
	c_name         CHAR(25) NOT NULL,
	c_address      VARCHAR(40) NOT NULL,
	c_nationkey    INTEGER NOT NULL,
	c_phone        CHAR(15) NOT NULL,
	c_acctbal      DECIMAL(15,2) NOT NULL,
	c_mktsegment   CHAR(10) NOT NULL,
	c_comment      VARCHAR(117) NOT NULL,

	FOREIGN KEY (c_nationkey) REFERENCES Nation(n_nationkey)
);

CREATE TABLE Supplier (
	s_suppkey      INTEGER NOT NULL PRIMARY KEY,
	s_name         CHAR(25) NOT NULL,
	s_address      VARCHAR(40) NOT NULL,
	s_nationkey    INTEGER NOT NULL,
	s_phone        CHAR(15) NOT NULL,
	s_acctbal      DECIMAL(15,2) NOT NULL,
	s_comment      VARCHAR(101) NOT NULL ,
	
	FOREIGN KEY (s_nationkey) REFERENCES Nation(n_nationkey)
);

CREATE TABLE Part (
	p_partkey      INTEGER NOT NULL PRIMARY KEY,
	p_name         VARCHAR(55) NOT NULL,
	p_mfgr         CHAR(25) NOT NULL,
	p_brand        CHAR(10) NOT NULL,
	p_type         VARCHAR(25) NOT NULL,
	p_Size         INTEGER NOT NULL,
	p_container    CHAR(10) NOT NULL,
	P_retailprice  DECIMAL(15,2) NOT NULL,
	p_comment      VARCHAR(23) NOT NULL 
);

CREATE TABLE Partsupp (
	ps_partkey     INTEGER NOT NULL,
	ps_suppkey     INTEGER NOT NULL,
	ps_availqty    INTEGER NOT NULL,
	ps_supplycost  DECIMAL(15,2)  NOT NULL,
	ps_comment     VARCHAR(199) NOT NULL ,

	PRIMARY KEY (ps_partkey , ps_suppkey) ,
	FOREIGN KEY (ps_partkey) REFERENCES Part(p_partkey) ,
	FOREIGN KEY (ps_suppkey) REFERENCES Supplier(s_suppkey)
);

CREATE TABLE Orders (
	o_orderkey      INTEGER NOT NULL PRIMARY KEY ,
	o_custkey       INTEGER NOT NULL,
	o_status        CHAR(1) NOT NULL,
	o_totalprice    DECIMAL(15,2)  NOT NULL,
	o_orderdate     DATE NOT NULL,
	o_orderpriority CHAR(15) NOT NULL,
	o_cleark        CHAR(15) NOT NULL,
	o_shippriority  INTEGER NOT NULL,
	o_comment       VARCHAR(79) NOT NULL , 
	
	FOREIGN KEY (o_custkey) REFERENCES Customer(c_custkey) 
);

CREATE TABLE Lineitem (
	l_orderkey       INTEGER NOT NULL,
	l_partkey        INTEGER NOT NULL,
	l_suppkey        INTEGER NOT NULL,
	l_linenumber     INTEGER NOT NULL,
	l_quantity       INTEGER NOT NULL,
	l_extendedprice  DECIMAL(15,2) NOT NULL,
	l_discount       DECIMAL(15,2) NOT NULL,
	l_tax            DECIMAL(15,2) NOT NULL,
	l_returnflag     CHAR(1) NOT NULL,
	l_linestatus     CHAR(1) NOT NULL,
	l_shipdata       DATE NOT NULL,
	l_commitdate     DATE NOT NULL,
	l_receiptdate    DATE NOT NULL,
	l_shipinstruct   CHAR(25) NOT NULL,
	l_shipmode       CHAR(10) NOT NULL,
	l_comment        VARCHAR(44) NOT NULL,

	FOREIGN KEY (l_orderkey) REFERENCES Orders(o_orderkey) ,
	FOREIGN KEY (l_partkey , l_suppkey) REFERENCES Partsupp(ps_partkey , ps_suppkey) 
);


-- SHOW VARIABLES LIKE 'secure_file_priv' ;
-- GRANT FILE ON *.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES; 


LOAD DATA INFILE '/var/lib/mysql-files/region.tbl' INTO TABLE Region FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/nation.tbl' INTO TABLE Nation FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/customer.tbl' INTO TABLE Customer FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/supplier.tbl' INTO TABLE Supplier FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n';   
            
LOAD DATA INFILE '/var/lib/mysql-files/part.tbl' INTO TABLE Part FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 
            
LOAD DATA INFILE '/var/lib/mysql-files/partsupp.tbl' INTO TABLE Partsupp FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/orders.tbl' INTO TABLE Orders FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 

LOAD DATA INFILE '/var/lib/mysql-files/lineitem.tbl' INTO TABLE Lineitem FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n'; 
            
-- SELECT COUNT(*) AS Regions_count FROM Region ;
-- SELECT COUNT(*) AS Nations_count FROM Nation ;
-- SELECT COUNT(*) AS Customers_count FROM Customer ;
-- SELECT COUNT(*) AS Suppliers_count FROM Supplier ;
-- SELECT COUNT(*) AS Parts_count FROM Part ;
-- SELECT COUNT(*) AS Parts_Suppliers_count FROM Partsupp ;
-- SELECT COUNT(*) AS Orders_count FROM Orders ;
-- SELECT COUNT(*) AS LineItems_count FROM Lineitem ;

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
