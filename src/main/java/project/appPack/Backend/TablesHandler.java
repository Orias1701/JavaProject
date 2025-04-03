package project.appPack.Backend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

public class TablesHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        if (!authenticate(exchange)) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(ORDERS_DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES " +
                 "WHERE TABLE_SCHEMA = 'orders' AND TABLE_TYPE = 'BASE TABLE'")) {
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
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Server error", e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }
}