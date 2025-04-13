package controller;

import com.sun.net.httpserver.HttpExchange;
import java.sql.Connection;

public interface TableDataInterface {
    void handleGet(HttpExchange exchange, Connection conn, String tableName) throws Exception;
    void handlePost(HttpExchange exchange, Connection conn, String tableName) throws Exception;
    void handlePut(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception;
    void handleDelete(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception;
}