package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.ApiClient.ApiResponse;
import model.TableDataOperationsClient;

public class CheckDetail {

    public static void main(String[] args) {
        String authHeader = "Bearer your-auth-token";
        TableDataOperationsClient client = new TableDataOperationsClient(authHeader);

        String tableName = "b0_kiemtrachitiet";
        String keyColumn = "TinhTrang";
        String keyValue = "Tot"; // hoặc "Hỏng"

        ApiResponse res = client.getRow(tableName, keyColumn, keyValue);

        if (res.isSuccess()) {
            System.out.println("✅ Dữ liệu dòng:");
            System.out.println(res.getMessage());

            if ("Tot".equals(keyValue)) {
                System.out.println("DenBu = 0 (TrangThai = Tot)");
                updateDenBu(0);
            } else if ("Hỏng".equals(keyValue)) {
                double denBuValue = getDenBuFromOtherTable();
                System.out.println("DenBu = " + denBuValue + " (TrangThai = Hỏng)");
                updateDenBu(denBuValue);
            }
        } else {
            System.err.println("❌ Lỗi khi lấy dữ liệu dòng: " + res.getMessage());
        }
    }

    // Sử dụng DatabaseUtil để lấy kết nối và cập nhật dữ liệu
    public static void updateDenBu(double denBuValue) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String updateSQL = "UPDATE b0_kiemtrachitiet SET DenBu = ? WHERE TinhTrang = 'Hỏng' OR TinhTrang = 'Tot'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setDouble(1, denBuValue);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " dòng dữ liệu đã được cập nhật.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy giá trị DenBu từ bảng khác bằng DatabaseUtil
    public static double getDenBuFromOtherTable() {
        double denBuValue = 0;
        try (Connection conn = DatabaseUtil.getConnection()) {
            String selectSQL = "SELECT DenBuColumn FROM OtherTable WHERE ConditionColumn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, "some_condition_value"); // Cập nhật điều kiện thực tế
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        denBuValue = rs.getDouble("DenBuColumn");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return denBuValue;
    }
}
