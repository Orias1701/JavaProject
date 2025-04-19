package controller;

import java.sql.*;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import model.TableDataOperationsClient;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;
public class CheckBooking {

    private final TableDataOperationsClient client;
    private final ContentPanel contentPanel;
    private final TablePanel tablePanel;

    public CheckBooking(String authToken, ContentPanel contentPanel, TablePanel tablePanel) {
        this.client = new TableDataOperationsClient(authToken);
        this.contentPanel = contentPanel;
        this.tablePanel = tablePanel;
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
            tablePanel.updateTableData(result.data, result.columnComments, keyColumn, tableName, "Thông tin đặt phòng");
            System.out.println("✔ Đã cập nhật trạng thái đặt phòng");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xử lý đặt phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processBooking(String tableName, String keyColumn, String keyValue) {
        // Xử lý nếu cần
    }

    private void updatePhongTheoTinhTrang() throws Exception {
    try (Connection conn = DatabaseUtil.getConnection()) {
        // 1. Chuẩn bị danh sách mã phòng đã xử lý
        Set<String> phongDaXuLy = new HashSet<>();

        String sql = """
            SELECT dp.MaDatPhong, dp.MaPhong, dp.NgayNhanPhong, dp.NgayTraPhong, dp.CachDat, dp.TinhTrang, p.TinhTrangPhong, kh.MaKhachHang
            FROM a6_datphong dp
            JOIN a5_phong p ON dp.MaPhong = p.MaPhong
            JOIN a1_khachhang kh ON dp.MaKhachHang = kh.MaKhachHang;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maPhong = rs.getString("MaPhong");
                String tinhTrangDat = rs.getString("TinhTrang");
                String cachDat = rs.getString("CachDat");
                String maKhachHang = rs.getString("MaKhachHang");

                phongDaXuLy.add(maPhong); // Đánh dấu đã xử lý

                LocalDateTime nhanPhong = rs.getTimestamp("NgayNhanPhong").toLocalDateTime();
                LocalDateTime traPhong = rs.getTimestamp("NgayTraPhong").toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();

                if (traPhong.isBefore(nhanPhong)) {
                    JOptionPane.showMessageDialog(null, "❌ Ngày trả phòng phải sau ngày nhận phòng (phòng " + maPhong + ")");
                    continue;
                }

                String newTinhTrangPhong = null;
                String newTinhTrangKhach = null;

                // Đặt trực tiếp
                if ("Đặt trực tiếp".equalsIgnoreCase(cachDat)) {
                    if ("Đang sử dụng".equalsIgnoreCase(tinhTrangDat)) {
                        newTinhTrangPhong = "Đang sử dụng";
                        newTinhTrangKhach = "Đang ở";
                    } else if ("Quá hạn".equalsIgnoreCase(tinhTrangDat)) {
                        if (now.isAfter(traPhong.plusMinutes(30))) {
                            long minutesLate = Duration.between(traPhong.plusMinutes(30), now).toMinutes();
                            double tienPhat = 300_000 * 0.3 * (minutesLate / 120.0); // giả định 300k/phòng
                            System.out.println("Tiền phạt phòng " + maPhong + ": " + tienPhat);
                            newTinhTrangPhong = "Đang sử dụng";
                            newTinhTrangKhach = "Đang ở";
                        } else {
                            newTinhTrangPhong = "Đang sử dụng";
                            newTinhTrangKhach = "Đang ở";
                        }
                    } else if ("Đang đợi".equalsIgnoreCase(tinhTrangDat)) {
                        JOptionPane.showMessageDialog(null, "⚠ Đặt trực tiếp không thể ở trạng thái Đang đợi (phòng " + maPhong + ")");
                        continue;
                    }
                }

                // Đặt online
                else if ("Đặt online".equalsIgnoreCase(cachDat)) {
                    if ("Quá hạn".equalsIgnoreCase(tinhTrangDat)) {
                        if (now.isAfter(traPhong.plusMinutes(30))) {
                            newTinhTrangPhong = "Trống";
                            newTinhTrangKhach = "Đã rời";
                        }
                    } else if ("Đang đợi".equalsIgnoreCase(tinhTrangDat)) {
                        LocalDateTime before12h = nhanPhong.minusHours(12);
                        if (now.isBefore(before12h)) {
                            newTinhTrangPhong = "Trống";
                            newTinhTrangKhach = "Đã rời";
                        } else {
                            newTinhTrangPhong = "Đã đặt";
                            newTinhTrangKhach = "Đang ở";
                        }
                    }
                }

                // Trạng thái "Đã trả"
                if ("Đã trả".equalsIgnoreCase(tinhTrangDat)) {
                    newTinhTrangPhong = "Trống";
                    newTinhTrangKhach = "Đã rời";
                }

                // Cập nhật phòng
                if (newTinhTrangPhong != null) {
                    try (PreparedStatement updatePhong = conn.prepareStatement("UPDATE a5_phong SET TinhTrangPhong = ? WHERE MaPhong = ?")) {
                        updatePhong.setString(1, newTinhTrangPhong);
                        updatePhong.setString(2, maPhong);
                        updatePhong.executeUpdate();
                        System.out.println("✔ Cập nhật phòng " + maPhong + ": " + newTinhTrangPhong);
                    }
                }

                // Cập nhật khách
                if (newTinhTrangKhach != null) {
                    try (PreparedStatement updateKhach = conn.prepareStatement("UPDATE a1_khachhang SET TinhTrangKhach = ? WHERE MaKhachHang = ?")) {
                        updateKhach.setString(1, newTinhTrangKhach);
                        updateKhach.setString(2, maKhachHang);
                        updateKhach.executeUpdate();
                        System.out.println("✔ Cập nhật khách " + maKhachHang + ": " + newTinhTrangKhach);
                    }
                }
            }
        }

        // ✅ Cập nhật các phòng không có trong đặt phòng → Trống
        String sqlAllPhong = "SELECT MaPhong FROM a5_phong";
        try (PreparedStatement psAll = conn.prepareStatement(sqlAllPhong);
             ResultSet rsAll = psAll.executeQuery()) {

            while (rsAll.next()) {
                String maPhong = rsAll.getString("MaPhong");
                if (!phongDaXuLy.contains(maPhong)) {
                    try (PreparedStatement updatePhong = conn.prepareStatement(
                            "UPDATE a5_phong SET TinhTrangPhong = 'Trống' WHERE MaPhong = ?")) {
                        updatePhong.setString(1, maPhong);
                        updatePhong.executeUpdate();
                        System.out.println("✔ Phòng " + maPhong + " không có đặt → cập nhật Trống");
                    }
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}


}