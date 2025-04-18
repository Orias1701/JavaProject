package controller;

import java.sql.*;
import java.time.*;
import javax.swing.JOptionPane;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import model.TableDataOperationsClient;
import view.MainRegion.ContentPanel;

public class CheckBooking {

    private final TableDataOperationsClient client;
    private final ContentPanel contentPanel;

    public CheckBooking(String authToken, ContentPanel contentPanel) {
        this.client = new TableDataOperationsClient(authToken);
        this.contentPanel = contentPanel;
    }

    public void autoProcessBooking(String tableName, String keyColumn, String keyValue) {
        try {
            processBooking(tableName, keyColumn, keyValue);
            TableDataResult result = ApiClient.getTableData(tableName);
            if (result == null || result.data == null) {
                JOptionPane.showMessageDialog(null, "Không thể lấy dữ liệu từ server", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            updatePhongTheoTinhTrang();
            contentPanel.updateTableData(result.data, result.columnComments, keyColumn, tableName, "Thông tin đặt phòng");
            System.out.println("✔ Đã cập nhật trạng thái đặt phòng");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xử lý đặt phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processBooking(String tableName, String keyColumn, String keyValue) {
        // Placeholder nếu cần xử lý trước khi update (API call...)
    }

    private void updatePhongTheoTinhTrang() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = """
                SELECT dp.MaDatPhong, dp.MaPhong, dp.NgayNhanPhong, dp.NgayTraPhong, dp.CachDat, dp.TinhTrang, p.TinhTrangPhong AS TTP
                FROM a6_datphong dp
                JOIN a5_phong p ON dp.MaPhong = p.MaPhong;
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String maPhong = rs.getString("MaPhong");
                    String tinhTrangDat = rs.getString("TinhTrang");
                    String cachDat = rs.getString("CachDat");
                    LocalDateTime nhanPhong = rs.getTimestamp("NgayNhanPhong").toLocalDateTime();
                    LocalDateTime traPhong = rs.getTimestamp("NgayTraPhong").toLocalDateTime();
                    LocalDateTime now = LocalDateTime.now();

                    String newTinhTrangPhong = null;

                    // 1. Kiểm tra đặt trực tiếp
                    if ("Đặt trực tiếp".equalsIgnoreCase(cachDat)) {
                        if ("Đang sử dụng".equalsIgnoreCase(tinhTrangDat)) {
                            newTinhTrangPhong = "Đang sử dụng";
                        } else if ("Quá hạn".equalsIgnoreCase(tinhTrangDat)) {
                            if (now.isAfter(traPhong.plusMinutes(30))) {
                                newTinhTrangPhong = "Đang sử dụng"; // vẫn đang ở trong
                                // Tiền phạt nếu vượt quá
                                long minutesLate = Duration.between(traPhong.plusMinutes(30), now).toMinutes();
                                double tienPhat = 0.5 * (minutesLate / 60.0); // Tính tiền phạt
                                System.out.println("Tiền phạt phòng " + maPhong + ": " + tienPhat);
                            } else {
                                newTinhTrangPhong = "Đang sử dụng";
                            }
                        } else if ("Đang đợi".equalsIgnoreCase(tinhTrangDat)) {
                            // Trực tiếp không được ở trạng thái đang đợi
                            JOptionPane.showMessageDialog(null, "⚠ Đặt trực tiếp không thể ở trạng thái Đang đợi (phòng " + maPhong + ")");
                            continue;
                        }
                    }

                    // 2. Đặt online
                    else if ("Đặt online".equalsIgnoreCase(cachDat)) {
                        if ("Quá hạn".equalsIgnoreCase(tinhTrangDat) && now.isAfter(traPhong.plusMinutes(30))) {
                            newTinhTrangPhong = "Trống"; // phòng quá hạn sẽ được giải phóng
                        } else if ("Đang đợi".equalsIgnoreCase(tinhTrangDat)) {
                            LocalDateTime before12Hours = nhanPhong.minusHours(12);
                            if (now.isBefore(before12Hours)) {
                                newTinhTrangPhong = "Trống";
                            } else {
                                newTinhTrangPhong = "Đã đặt";
                            }
                        }
                    }

                    // 3. Điều kiện nhận - trả hợp lệ
                    if (traPhong.isBefore(nhanPhong)) {
                        JOptionPane.showMessageDialog(null, "❌ Ngày trả phòng phải sau ngày nhận phòng (phòng " + maPhong + ")");
                        continue;
                    }

                    // 4. Cập nhật trạng thái phòng nếu có thay đổi
                    if (newTinhTrangPhong != null) {
                        String updatePhong = "UPDATE a5_phong SET TinhTrangPhong = ? WHERE MaPhong = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updatePhong)) {
                            updateStmt.setString(1, newTinhTrangPhong);
                            updateStmt.setString(2, maPhong);
                            int rows = updateStmt.executeUpdate();
                            if (rows > 0) {
                                System.out.println("✔ Cập nhật trạng thái phòng " + maPhong + ": " + newTinhTrangPhong);
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
