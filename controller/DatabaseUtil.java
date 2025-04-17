package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {
    // Lấy tên schema (tên database) từ URL kết nối
    private static final String SCHEMA = BaseHandler.DATA_DB_URL.substring(BaseHandler.DATA_DB_URL.lastIndexOf("/") + 1);

    // Kết nối đến database bằng thông tin trong BaseHandler.
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(BaseHandler.DATA_DB_URL, BaseHandler.DB_USERNAME, BaseHandler.DB_PASSWORD);
    }

    // Kiểm tra xem bảng có tồn tại trong schema không.
    public static boolean isTableExists(Connection conn, String tableName) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        stmt.setString(1, SCHEMA);          // Gán schema (database name)
        stmt.setString(2, tableName);       // Gán tên bảng
        ResultSet rs = stmt.executeQuery(); // Thực thi truy vấn
        boolean exists = rs.next();         // Nếu có dòng trả về => bảng tồn tại
        LogHandler.logInfo("Kiểm tra bảng " + tableName + ": " + (exists ? "tồn tại" : "không tồn tại"));
        return exists;
    }

    // Lấy tên cột khóa chính của bảng.
    public static String getKeyColumn(Connection conn, String tableName) throws Exception {
        PreparedStatement pkStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'"
        );
        pkStmt.setString(1, SCHEMA);
        pkStmt.setString(2, tableName);
        ResultSet pkRs = pkStmt.executeQuery();
        String keyColumn = pkRs.next() ? pkRs.getString("COLUMN_NAME") : null;
        LogHandler.logInfo("Truy vấn khóa chính cho " + tableName + ": " + (keyColumn != null ? keyColumn : "không tìm thấy"));
        return keyColumn;
    }

    // Lấy thông tin mô tả (comment) của các cột trong bảng.
    // Nếu không có comment thì sẽ dùng tên cột làm comment mặc định.
    public static Map<String, String> getColumnMetadata(Connection conn, String tableName) throws Exception {
        Map<String, String> columnMetadata = new HashMap<>();
        PreparedStatement metaStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        metaStmt.setString(1, SCHEMA);
        metaStmt.setString(2, tableName);
        ResultSet metaRs = metaStmt.executeQuery();
        while (metaRs.next()) {
            String columnName = metaRs.getString("COLUMN_NAME");
            String comment = metaRs.getString("COLUMN_COMMENT");
            // Nếu comment bị null hoặc rỗng thì dùng tên cột làm comment mặc định
            if (comment == null || comment.isEmpty()) comment = columnName;
            columnMetadata.put(columnName, comment);
        }
        return columnMetadata;
    }

    // Lấy danh sách tên các cột hợp lệ trong bảng.
    // Dùng để kiểm tra các cột hợp lệ khi thao tác dữ liệu.
    public static Map<String, Boolean> getValidColumns(Connection conn, String tableName) throws Exception {
        Map<String, Boolean> validColumns = new HashMap<>();
        PreparedStatement metaStmt = conn.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        metaStmt.setString(1, SCHEMA);
        metaStmt.setString(2, tableName);
        ResultSet metaRs = metaStmt.executeQuery();
        while (metaRs.next()) {
            validColumns.put(metaRs.getString("COLUMN_NAME"), true);
        }
        return validColumns;
    }
}
