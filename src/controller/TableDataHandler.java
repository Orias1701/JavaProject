package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        try (Connection conn = DriverManager.getConnection(DATA_DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Lấy comment cột
            StringBuilder columnsJson = new StringBuilder("[");
            PreparedStatement metaStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
            );
            metaStmt.setString(1, "ql_khachsan");
            metaStmt.setString(2, tableName);
            ResultSet metaRs = metaStmt.executeQuery();
            boolean firstColumn = true;
            while (metaRs.next()) {
                if (!firstColumn) columnsJson.append(",");
                String columnName = metaRs.getString("COLUMN_NAME");
                String comment = metaRs.getString("COLUMN_COMMENT");
                if (comment == null || comment.isEmpty()) comment = columnName;
                columnsJson.append("{\"name\":\"").append(columnName)
                           .append("\",\"comment\":\"").append(comment.replace("\"", "\\\""))
                           .append("\"}");
                firstColumn = false;
            }
            columnsJson.append("]");

            // Lấy dữ liệu hàng
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = stmt.executeQuery();
            StringBuilder dataJson = new StringBuilder("[");
            boolean firstRow = true;
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                if (!firstRow) dataJson.append(",");
                dataJson.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);
                    if (i > 1) dataJson.append(",");
                    dataJson.append("\"").append(columnName).append("\":\"")
                           .append(value != null ? value.replace("\"", "\\\"") : "").append("\"");
                }
                dataJson.append("}");
                firstRow = false;
            }
            dataJson.append("]");

            // Kết hợp JSON
            String json = "{\"columns\":" + columnsJson + ",\"data\":" + dataJson + "}";
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Server error", e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }
}