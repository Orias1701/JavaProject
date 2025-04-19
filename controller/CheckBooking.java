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
            Set<String> phongDaXuLy = new HashSet<>();
    
            String sql = """
                SELECT dp.MaDatPhong, dp.MaPhong, dp.NgayNhanPhong, dp.NgayTraPhong, dp.CachDat, dp.TinhTrang, p.TinhTrangPhong, kh.MaKhachHang
                FROM c3_datphong dp
                JOIN c2_phong p ON dp.MaPhong = p.MaPhong
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
                    double tienPhong = 0; // Biến lưu tiền phòng
                    double tienPhat = 0;  // Biến lưu tiền phạt
    
                    // Truy vấn tiền phòng từ loại phòng
                    String sqlLoaiPhong = """
                        SELECT lp.GiaLoai
                        FROM c2_phong p
                        JOIN c1_loaiphong lp ON p.MaLoai = lp.MaLoai
                        WHERE p.MaPhong = ?;
                    """;
    
                    try (PreparedStatement psLoaiPhong = conn.prepareStatement(sqlLoaiPhong)) {
                        psLoaiPhong.setString(1, maPhong);
                        try (ResultSet rsLoaiPhong = psLoaiPhong.executeQuery()) {
                            if (rsLoaiPhong.next()) {
                                tienPhong = rsLoaiPhong.getDouble("GiaLoai");
                            }
                        }
                    }
    
                    // Đặt trực tiếp
                    if ("Đặt trực tiếp".equalsIgnoreCase(cachDat)) {
                        if ("Đang sử dụng".equalsIgnoreCase(tinhTrangDat)) {
                            newTinhTrangPhong = "Đang sử dụng";
                            newTinhTrangKhach = "Đang ở";
                        } else if ("Quá hạn".equalsIgnoreCase(tinhTrangDat)) {
                            if (now.isAfter(traPhong.plusMinutes(30))) {
                                long minutesLate = Duration.between(traPhong.plusMinutes(30), now).toMinutes();
                                tienPhat = 300_000 * 0.3 * (minutesLate / 120.0);
                                System.out.println("Tiền phạt phòng " + maPhong + ": " + tienPhat);
                            
                                // ✅ Cập nhật tiền phạt vào bảng đặt phòng
                                String updateTienPhatSql = "UPDATE c3_datphong SET TienPhat = ? WHERE MaDatPhong = ?";
                                try (PreparedStatement updateTienPhat = conn.prepareStatement(updateTienPhatSql)) {
                                    updateTienPhat.setDouble(1, tienPhat);
                                    updateTienPhat.setString(2, rs.getString("MaDatPhong"));
                                    updateTienPhat.executeUpdate();
                                    System.out.println("✔ Cập nhật tiền phạt " + tienPhat + " cho đơn " + rs.getString("MaDatPhong"));
                                }
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
                        try (PreparedStatement updatePhong = conn.prepareStatement("UPDATE c2_phong SET TinhTrangPhong = ? WHERE MaPhong = ?")) {
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
    
                    // In thông tin tiền phòng và tiền phạt
                    System.out.println("✔ Tiền phòng cho phòng " + maPhong + ": " + tienPhong);
                    System.out.println("✔ Tiền phạt cho phòng " + maPhong + ": " + tienPhat);
                }
            }
    
            // ✅ Cập nhật các phòng không có trong đặt phòng → Trống
            String sqlAllPhong = "SELECT MaPhong FROM c2_phong";
            try (PreparedStatement psAll = conn.prepareStatement(sqlAllPhong);
                 ResultSet rsAll = psAll.executeQuery()) {
    
                while (rsAll.next()) {
                    String maPhong = rsAll.getString("MaPhong");
                    if (!phongDaXuLy.contains(maPhong)) {
                        try (PreparedStatement updatePhong = conn.prepareStatement(
                                "UPDATE c2_phong SET TinhTrangPhong = 'Trống' WHERE MaPhong = ?")) {
                            updatePhong.setString(1, maPhong);
                            updatePhong.executeUpdate();
                            System.out.println("✔ Phòng " + maPhong + " không có đặt → cập nhật Trống");
                        }
                    }
                }
            }
            // ✅ Cập nhật khách không có trong bảng đặt phòng → Đã rời
            String sqlAllKhach = "SELECT MaKhachHang FROM a1_khachhang";
            Set<String> khachDaXuLy = new HashSet<>();

            // Thu thập mã khách đã có đặt phòng
            String sqlKhachDat = "SELECT DISTINCT MaKhachHang FROM c3_datphong";
            try (PreparedStatement psDat = conn.prepareStatement(sqlKhachDat);
                ResultSet rsDat = psDat.executeQuery()) {
                while (rsDat.next()) {
                    khachDaXuLy.add(rsDat.getString("MaKhachHang"));
                }
            }

            // Lọc ra những khách không có trong đặt phòng
            try (PreparedStatement psAllKhach = conn.prepareStatement(sqlAllKhach);
                ResultSet rsAllKhach = psAllKhach.executeQuery()) {
                while (rsAllKhach.next()) {
                    String maKhach = rsAllKhach.getString("MaKhachHang");
                    if (!khachDaXuLy.contains(maKhach)) {
                        try (PreparedStatement updateKhach = conn.prepareStatement(
                                "UPDATE a1_khachhang SET TinhTrangKhach = 'Đã rời' WHERE MaKhachHang = ?")) {
                            updateKhach.setString(1, maKhach);
                            updateKhach.executeUpdate();
                            System.out.println("✔ Khách " + maKhach + " không còn đặt phòng → cập nhật 'Đã rời'");
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