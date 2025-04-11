package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TablesHandler extends BaseHandler {
    private static final Logger LOGGER = Logger.getLogger(TablesHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        if (!authenticate(exchange)) return;

        String schema = extractSchemaFromUrl(DATA_DB_URL);

        try (Connection conn = DriverManager.getConnection(DATA_DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(
                 "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES " +
                 "WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'")) {
            stmt.setString(1, schema);
            LOGGER.info("Executing query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                String name = rs.getString("TABLE_NAME");
                String comment = rs.getString("TABLE_COMMENT");
                if (comment == null || comment.isEmpty()) comment = name;
                json.append("{\"name\":\"").append(name).append("\",\"comment\":\"").append(comment).append("\"}");
                first = false;
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Server error", e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private String extractSchemaFromUrl(String url) {
        if (url == null || !url.contains("/")) return "ql_khachsan";
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\?")[0];
    }
}