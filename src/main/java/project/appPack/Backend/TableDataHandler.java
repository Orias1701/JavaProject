package project.appPack.Backend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

public class TableDataHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        if (!authenticate(exchange)) {
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String tableName = path.substring("/api/table/".length());
        if (tableName.isEmpty()) {
            sendResponse(exchange, 400, "Bad Request: Table name missing");
            return;
        }

        try (Connection conn = DriverManager.getConnection(ORDERS_DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName)) {
            ResultSet rs = stmt.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean firstRow = true;
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                if (!firstRow) json.append(",");
                json.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);
                    if (i > 1) json.append(",");
                    json.append("\"").append(columnName).append("\":\"").append(value != null ? value : "").append("\"");
                }
                json.append("}");
                firstRow = false;
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString());
        } catch (Exception e) {
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Server error", e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }
}