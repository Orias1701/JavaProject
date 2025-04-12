package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableDataHandler extends BaseHandler {

    private static final Logger LOGGER = Logger.getLogger(TableDataHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String tableName = path.startsWith("/api/table/") ? path.substring("/api/table/".length()) : "";
        String[] pathParts = tableName.split("/");

        if (!authenticate(exchange)) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DATA_DB_URL, DB_USERNAME, DB_PASSWORD)) {
            if (!isTableExists(conn, pathParts[0])) {
                sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Bảng không tồn tại");
                return;
            }

            switch (method) {
                case "GET":
                    handleGet(exchange, conn, pathParts[0]);
                    break;
                case "POST":
                    handlePost(exchange, conn, pathParts[0]);
                    break;
                case "PUT":
                    if (pathParts.length < 2) {
                        sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Thiếu giá trị khóa chính");
                        return;
                    }
                    handlePut(exchange, conn, pathParts[0], pathParts[1]);
                    break;
                case "DELETE":
                    if (pathParts.length < 2) {
                        sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Thiếu giá trị khóa chính");
                        return;
                    }
                    handleDelete(exchange, conn, pathParts[0], pathParts[1]);
                    break;
                default:
                    sendResponse(exchange, 405, "Phương thức không được phép");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi server: " + e.getMessage(), e);
            sendResponse(exchange, 500, "Lỗi server: " + e.getMessage());
        }
    }

    private boolean isTableExists(Connection conn, String tableName) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        stmt.setString(1, "ql_khachsan");
        stmt.setString(2, tableName);
        ResultSet rs = stmt.executeQuery();
        boolean exists = rs.next();
        LOGGER.info("Kiểm tra bảng " + tableName + ": " + (exists ? "tồn tại" : "không tồn tại"));
        return exists;
    }

    private void handleGet(HttpExchange exchange, Connection conn, String tableName) throws Exception {
        String keyColumn = getKeyColumn(conn, tableName);
        System.out.println("Khóa chính Handler: " + keyColumn);
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

        String json = "{\"keyColumn\":\"" + (keyColumn != null ? keyColumn : "") + "\",\"columns\":" + columnsJson + ",\"data\":" + dataJson + "}";
        sendResponse(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange, Connection conn, String tableName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();

        Map<String, String> data = parseJson(requestBody.toString());
        if (data.isEmpty()) {
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        PreparedStatement metaStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        metaStmt.setString(1, "ql_khachsan");
        metaStmt.setString(2, tableName);
        ResultSet metaRs = metaStmt.executeQuery();
        Map<String, Boolean> validColumns = new HashMap<>();
        while (metaRs.next()) {
            validColumns.put(metaRs.getString("COLUMN_NAME"), true);
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        boolean first = true;
        for (String column : data.keySet()) {
            if (!validColumns.containsKey(column)) {
                sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Cột không hợp lệ " + column);
                return;
            }
            if (!first) {
                columns.append(",");
                placeholders.append(",");
            }
            columns.append(column);
            placeholders.append("?");
            first = false;
        }

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        PreparedStatement stmt = conn.prepareStatement(sql);
        int index = 1;
        for (String column : data.keySet()) {
            stmt.setString(index++, data.get(column));
        }

        try {
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sendResponse(exchange, 201, "{\"message\":\"Thêm hàng thành công\"}");
            } else {
                sendResponse(exchange, 500, "Lỗi server: Không thể thêm hàng");
            }
        } catch (java.sql.SQLException e) {
            if (e.getErrorCode() == 1062) {
                sendResponse(exchange, 409, "Lỗi: Giá trị trùng lặp");
            } else {
                throw e;
            }
        }
    }

    private void handlePut(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception {
        LOGGER.info("Xử lý PUT cho bảng: " + tableName + ", Giá trị khóa chính: " + keyValue);

        String keyColumn = getKeyColumn(conn, tableName);
        if (keyColumn == null) {
            LOGGER.warning("Không tìm thấy khóa chính cho bảng: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Bảng không có khóa chính");
            return;
        }
        LOGGER.info("Khóa chính: " + keyColumn);

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();
        LOGGER.info("Body JSON: " + requestBody);

        Map<String, String> data = parseJson(requestBody.toString());
        if (data.isEmpty()) {
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        PreparedStatement metaStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        metaStmt.setString(1, "ql_khachsan");
        metaStmt.setString(2, tableName);
        ResultSet metaRs = metaStmt.executeQuery();
        Map<String, Boolean> validColumns = new HashMap<>();
        while (metaRs.next()) {
            validColumns.put(metaRs.getString("COLUMN_NAME"), true);
        }

        StringBuilder setClause = new StringBuilder();
        boolean first = true;
        for (String column : data.keySet()) {
            if (!validColumns.containsKey(column)) {
                sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Cột không hợp lệ " + column);
                return;
            }
            if (!first) {
                setClause.append(",");
            }
            setClause.append(column).append("=?");
            first = false;
        }

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + keyColumn + "=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        int index = 1;
        for (String column : data.keySet()) {
            stmt.setString(index++, data.get(column));
        }
        stmt.setString(index, keyValue);

        try {
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Cập nhật hàng thành công\"}");
            } else {
                sendResponse(exchange, 404, "Không tìm thấy: Hàng với giá trị khóa chính " + keyValue + " không tồn tại");
            }
        } catch (java.sql.SQLException e) {
            if (e.getErrorCode() == 1062) {
                sendResponse(exchange, 409, "Lỗi: Giá trị trùng lặp");
            } else {
                throw e;
            }
        }
    }

    private void handleDelete(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception {
        String keyColumn = getKeyColumn(conn, tableName);
        if (keyColumn == null) {
            LOGGER.warning("Không tìm thấy khóa chính cho bảng: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Bảng không có khóa chính");
            return;
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + keyColumn + "=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, keyValue);

        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            sendResponse(exchange, 200, "{\"message\":\"Xóa hàng thành công\"}");
        } else {
            sendResponse(exchange, 404, "Không tìm thấy: Hàng với giá trị khóa chính " + keyValue + " không tồn tại");
        }
    }

    private String getKeyColumn(Connection conn, String tableName) throws Exception {
        PreparedStatement pkStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'"
        );
        pkStmt.setString(1, "ql_khachsan");
        pkStmt.setString(2, tableName);
        ResultSet pkRs = pkStmt.executeQuery();
        String keyColumn = pkRs.next() ? pkRs.getString("COLUMN_NAME") : null;
        LOGGER.info("Truy vấn khóa chính cho " + tableName + ": " + (keyColumn != null ? keyColumn : "không tìm thấy"));
        return keyColumn;
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                return result;
            }
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length != 2) continue;
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                result.put(key, value);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi phân tích JSON: " + json, e);
        }
        return result;
    }
}