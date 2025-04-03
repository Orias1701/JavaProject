-- Active: 1743462407381@@127.0.0.1@3306@khachsan
USE khachsan;
USE orders;

CREATE TABLE IF NOT EXISTS Customers (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Customer_ID VARCHAR(255) NOT NULL,
    Customer_Name VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) NOT NULL,
    Birthday DATE,
    UNIQUE (Customer_ID)
) COMMENT = 'Khách hàng';
ALTER TABLE Customers COMMENT = 'Khách hàng';


CREATE TABLE IF NOT EXISTS Services (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Service_ID VARCHAR(255) NOT NULL,
    Service_Name VARCHAR(255) NOT NULL,
    Price DECIMAL(10, 2),
    UNIQUE (Service_ID)
) COMMENT = 'Dịch vụ';
ALTER TABLE Services COMMENT = 'Dịch vụ';

CREATE TABLE IF NOT EXISTS Orders (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Order_ID VARCHAR(255) NOT NULL,
    Customer_ID VARCHAR(255) NOT NULL,
    Service_ID VARCHAR(255) NOT NULL,
    Order_date DATE,
    Price DECIMAL(10, 2),
    UNIQUE (Order_ID)
) COMMENT = 'Đơn hàng';

USE accounts;
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL
) COMMENT 'Danh sách user';

