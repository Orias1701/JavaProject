package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class TableDataOperations {
    public void handlePost(HttpExchange exchange, Connection conn, String tableName) throws Exception {
        // Đọc body yêu cầu
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();

        // Log nội dung body
        LogHandler.logInfo("POST request body for table " + tableName + ": " + requestBody.toString());

        // Kiểm tra body rỗng
        if (requestBody.length() == 0) {
            LogHandler.logError("POST request body is empty for table: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Body rỗng");
            return;
        }

        // Kiểm tra Content-Type
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (!"application/json".equalsIgnoreCase(contentType)) {
            LogHandler.logError("Invalid Content-Type: " + contentType + " for table: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Content-Type phải là application/json");
            return;
        }

        // Parse JSON
        Map<String, String> data;
        try {
            data = JsonUtil.parseJson(requestBody.toString());
        } catch (Exception e) {
            LogHandler.logError("JSON parse failed for table " + tableName + ": " + e.getMessage() + ", Input: " + requestBody.toString());
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        if (data.isEmpty()) {
            LogHandler.logError("Parsed JSON is empty for table " + tableName + ", Input: " + requestBody.toString());
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        Map<String, Boolean> validColumns = DatabaseUtil.getValidColumns(conn, tableName);
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        boolean first = true;
        for (String column : data.keySet()) {
            if (!validColumns.containsKey(column)) {
                LogHandler.logError("Invalid column " + column + " for table: " + tableName);
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
            LogHandler.logError("SQL error during POST for table " + tableName + ": " + e.getMessage());
            if (e.getErrorCode() == 1062) {
                sendResponse(exchange, 409, "Lỗi: Giá trị trùng lặp");
            } else {
                throw e;
            }
        }
    }

    public void handlePut(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception {
        LogHandler.logInfo("Xử lý PUT cho bảng: " + tableName + ", Giá trị khóa chính: " + keyValue);

        String keyColumn = DatabaseUtil.getKeyColumn(conn, tableName);
        if (keyColumn == null) {
            LogHandler.logError("Không tìm thấy khóa chính cho bảng: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Bảng không có khóa chính");
            return;
        }
        LogHandler.logInfo("Khóa chính: " + keyColumn);

        // Đọc body yêu cầu
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();
        LogHandler.logInfo("PUT request body for table " + tableName + ": " + requestBody.toString());

        // Kiểm tra body rỗng
        if (requestBody.length() == 0) {
            LogHandler.logError("PUT request body is empty for table: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Body rỗng");
            return;
        }

        // Kiểm tra Content-Type
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (!"application/json".equalsIgnoreCase(contentType)) {
            LogHandler.logError("Invalid Content-Type: " + contentType + " for table: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Content-Type phải là application/json");
            return;
        }

        // Parse JSON
        Map<String, String> data;
        try {
            data = JsonUtil.parseJson(requestBody.toString());
        } catch (Exception e) {
            LogHandler.logError("JSON parse failed for table " + tableName + ": " + e.getMessage() + ", Input: " + requestBody.toString());
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        if (data.isEmpty()) {
            LogHandler.logError("Parsed JSON is empty for table " + tableName + ", Input: " + requestBody.toString());
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Dữ liệu JSON không hợp lệ");
            return;
        }

        Map<String, Boolean> validColumns = DatabaseUtil.getValidColumns(conn, tableName);
        StringBuilder setClause = new StringBuilder();
        boolean first = true;
        for (String column : data.keySet()) {
            if (!validColumns.containsKey(column)) {
                LogHandler.logError("Invalid column " + column + " for table: " + tableName);
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
            LogHandler.logError("SQL error during PUT for table " + tableName + ": " + e.getMessage());
            if (e.getErrorCode() == 1062) {
                sendResponse(exchange, 409, "Lỗi: Giá trị trùng lặp");
            } else {
                throw e;
            }
        }
    }

    public void handleDelete(HttpExchange exchange, Connection conn, String tableName, String keyValue) throws Exception {
        LogHandler.logInfo("Xử lý DELETE cho bảng: " + tableName + ", Giá trị khóa chính: " + keyValue);

        String keyColumn = DatabaseUtil.getKeyColumn(conn, tableName);
        if (keyColumn == null) {
            LogHandler.logError("Không tìm thấy khóa chính cho bảng: " + tableName);
            sendResponse(exchange, 400, "Yêu cầu không hợp lệ: Bảng không có khóa chính");
            return;
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + keyColumn + "=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, keyValue);

        try {
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Xóa hàng thành công\"}");
            } else {
                sendResponse(exchange, 404, "Không tìm thấy: Hàng với giá trị khóa chính " + keyValue + " không tồn tại");
            }
        } catch (java.sql.SQLException e) {
            LogHandler.logError("SQL error during DELETE for table " + tableName + ": " + e.getMessage());
            throw e;
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}