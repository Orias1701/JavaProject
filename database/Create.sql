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
);

CREATE TABLE IF NOT EXISTS Services (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Service_ID VARCHAR(255) NOT NULL,
    Service_Name VARCHAR(255) NOT NULL,
    Price DECIMAL(10, 2),
    UNIQUE (Service_ID)
);

CREATE TABLE IF NOT EXISTS Orders (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Order_ID VARCHAR(255) NOT NULL,
    Customer_ID VARCHAR(255) NOT NULL,
    Service_ID VARCHAR(255) NOT NULL,
    Order_date DATE,
    Price DECIMAL(10, 2),
    UNIQUE (Order_ID)
);

CREATE TRIGGER before_insert_Customers
BEFORE INSERT ON Customers
FOR EACH ROW
BEGIN
    IF NEW.ID IS NULL THEN
        SET NEW.ID = (SELECT IFNULL(MAX(ID), 0) + 1 FROM Customers);
    END IF;
END $$

CREATE TRIGGER before_insert_services
BEFORE INSERT ON Services
FOR EACH ROW
BEGIN
    IF NEW.ID IS NULL THEN
        SET NEW.ID = (SELECT IFNULL(MAX(ID), 0) + 1 FROM Services);
    END IF;
END $$

CREATE TRIGGER before_insert_Orders
BEFORE INSERT ON Orders
FOR EACH ROW
BEGIN
    IF NEW.ID IS NULL THEN
        SET NEW.ID = (SELECT IFNULL(MAX(ID), 0) + 1 FROM Orders);
    END IF;
END $$
