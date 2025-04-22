package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import model.ApiClient;
import model.ApiClient.ApiResponse;
import model.TableDataOperationsClient;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;
import java.math.BigDecimal;
public class CheckBill {

    private final TableDataOperationsClient client;
    private ContentPanel contentPanel; // Tham chiếu đến ContentPanel để cập nhật dữ liệu
    private TablePanel tablePanel; // Tham chiếu đến TablePanel để làm mới bảng

    // Constructor để khởi tạo với authToken, contentPanel và tablePanel
    public CheckBill(String authToken, ContentPanel contentPanel, TablePanel tablePanel) {
        this.client = new TableDataOperationsClient(authToken);
        this.contentPanel = contentPanel;
        this.tablePanel = tablePanel;
    }

    /**
     * Xử lý chi tiết hóa đơn và tính lại TongTien dựa trên loại chi phí.
     *
     * @param tableName Tên bảng (b2_hoadonchitiet)
     * @param keyColumn Tên cột khóa (MaHoaDon)
     * @param keyValue Giá trị khóa (MaHoaDon)
     * @return ApiResponse Kết quả xử lý
     */
    public ApiResponse processCheckBill(String tableName, String keyColumn, String keyValue) {
        ApiResponse res;

        try {
            // Kiểm tra dữ liệu trước khi xử lý
            if (!isDataValid(tableName, keyColumn, keyValue)) {
                showErrorDialog("❌ Dữ liệu không hợp lệ hoặc không tồn tại.");
                return new ApiResponse(false, "Dữ liệu không hợp lệ");
            }

            if (keyValue == null || keyValue.trim().isEmpty()) {
                // Nếu không có keyValue, lấy tất cả dữ liệu
                res = client.getRow(tableName, keyColumn, keyValue);
            } else {
                // Nếu có keyValue, truy vấn theo key
                res = client.getRow(tableName, keyColumn, keyValue);
            }

            if (res.isSuccess()) {
                System.out.println("✅ Dữ liệu dòng:");
                System.out.println(res.getMessage());

                updateTongTienHoaDonChiTiet(); // Cập nhật TongTien trong b2_hoadonchitiet
                tablePanel.refreshTable(); // Làm mới bảng hiển thị
            } else {
                System.err.println("Lỗi: " + res.getMessage());
            }

            return res;
        } catch (Exception e) {
            showErrorDialog("Lỗi khi xử lý: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, e.getMessage());
        }
    }

    /**
     * Hàm tự động xử lý và cập nhật TongTien cho hóa đơn chi tiết.
     *
     * @param tableName Tên bảng (b2_hoadonchitiet)
     * @param keyColumn Tên cột khóa (MaHoaDon)
     * @param keyValue Giá trị khóa (MaHoaDon)
     */
    public void autoProcessCheckBill(String tableName, String keyColumn, String keyValue) {
        try {
            // Kiểm tra dữ liệu trước khi xử lý
            if (!isDataValid(tableName, keyColumn, keyValue)) {
                showErrorDialog("❌ Dữ liệu không hợp lệ hoặc không tồn tại.");
                return;
            }

            processCheckBill(tableName, keyColumn, keyValue);
            // Lấy lại dữ liệu sau khi cập nhật
            ApiClient.TableDataResult result = ApiClient.getTableData(tableName);
            if (result == null || result.data == null) {
                showErrorDialog("Không thể lấy dữ liệu từ server");
                return;
            }

            // Cập nhật TongTien
            updateTongTienHoaDonChiTiet();
            // Cập nhật bảng hiển thị
            contentPanel.updateTableData(result.data, result.columnComments, result.columnTypes, 
                keyColumn, tableName, "Chi tiết hóa đơn");

            System.out.println("Done check bill");
        } catch (Exception e) {
            showErrorDialog("Lỗi khi xử lý: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật cột TongTien trong bảng b2_hoadonchitiet dựa trên loại chi phí.
     * - Dịch vụ: Lấy từ e2_sddv.TongTien
     * - Phòng: GiaLoai * (NgayHenTra - NgayNhanPhong) + TienPhat
     * - Đền bù: Lấy từ d3_kiemtraphong.TongTien
     */
    private void updateTongTienHoaDonChiTiet() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Lấy dữ liệu từ b2_hoadonchitiet và các bảng liên quan để tính TongTien
            String selectQuery = """
                SELECT 
                    hdc.MaHoaDon, hdc.MaPhong, hdc.MaSDDV, hdc.MaKiemTra, hdc.MaDatPhong,
                    sddv.TongTien AS TongTienSDDV,
                    DATEDIFF(dp.NgayHen, dp.NgayNhanPhong) AS SoNgayThue,
                    lp.GiaLoai,
                    dp.TienPhat,
                    kt.TongTien AS TongTienDenBu
                FROM b2_hoadonchitiet hdc
                LEFT JOIN e2_sddv sddv ON hdc.MaSDDV = sddv.MaSDDV
                LEFT JOIN c3_datphong dp ON hdc.MaDatPhong = dp.MaDatPhong
                LEFT JOIN c2_phong p ON dp.MaPhong = p.MaPhong
                LEFT JOIN c1_loaiphong lp ON p.MaLoai = lp.MaLoai
                LEFT JOIN d3_kiemtraphong kt ON hdc.MaKiemTra = kt.MaKiemTra
            """;

            Map<String, Double> tongTienMap = new HashMap<>();

            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("MaHoaDon");
                    String maPhong = rs.getString("MaPhong");
                    String loaiChiPhi = determineLoaiChiPhi(rs.getString("MaSDDV"), 
                        rs.getString("MaDatPhong"), rs.getString("MaKiemTra"));
                    double tongTien = 0;

                    if ("Dịch vụ".equalsIgnoreCase(loaiChiPhi) && rs.getString("MaSDDV") != null) {
                        tongTien = rs.getBigDecimal("TongTien");
                    } else if ("Phòng".equalsIgnoreCase(loaiChiPhi) && rs.getString("MaDatPhong") != null) {
                        int soNgayThue = rs.getInt("SoNgayThue");
                        double giaLoai = rs.getObject("GiaLoai") != null ? rs.getDouble("GiaLoai") : 0;
                        double tienPhat = rs.getObject("TienPhat") != null ? rs.getDouble("TienPhat") : 0;
                        BigDecimal giaLoaiBD = rs.getBigDecimal("GiaLoai") != null ? rs.getBigDecimal("GiaLoai") : BigDecimal.ZERO;
                        BigDecimal tienPhatBD = rs.getBigDecimal("TienPhat") != null ? rs.getBigDecimal("TienPhat") : BigDecimal.ZERO;
                        BigDecimal soNgayThueBD = BigDecimal.valueOf(soNgayThue);
                        tongTien = giaLoaiBD.multiply(soNgayThueBD).add(tienPhatBD).doubleValue();
                    } else if ("Đền bù".equalsIgnoreCase(loaiChiPhi) && rs.getString("MaKiemTra") != null) {
                        tongTien = rs.getDouble("TongTien");
                    }

                    tongTienMap.put(maHoaDon + "|" + maPhong, tongTien);
                }
            }

            // Cập nhật TongTien vào b2_hoadonchitiet
            String updateQuery = """
                UPDATE b2_hoadonchitiet
                SET TongTien = ?
                WHERE MaHoaDon = ? AND MaPhong = ?
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                for (Map.Entry<String, Double> entry : tongTienMap.entrySet()) {
                    String[] keys = entry.getKey().split("\\|");
                    String maHoaDon = keys[0];
                    String maPhong = keys[1];
                    double tongTien = entry.getValue();

                    pstmt.setDouble(1, tongTien);
                    pstmt.setString(2, maHoaDon);
                    pstmt.setString(3, maPhong);
                    pstmt.addBatch();
                }

                int[] rows = pstmt.executeBatch();
                System.out.println("✔ Đã cập nhật " + rows.length + " dòng trong bảng b2_hoadonchitiet (TongTien)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi cập nhật TongTien: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Kiểm tra dữ liệu trong b2_hoadonchitiet có hợp lệ không.
     *
     * @param tableName Tên bảng
     * @param keyColumn Tên cột khóa
     * @param keyValue Giá trị khóa
     * @return true nếu dữ liệu hợp lệ, false nếu không
     */
    private boolean isDataValid(String tableName, String keyColumn, String keyValue) throws SQLException, Exception {
        if (!tableName.equals("b2_hoadonchitiet")) {
            System.err.println("Bảng không hợp lệ: " + tableName);
            return false;
        }
        System.out.println("Bảng hợp lệ: " + tableName);

        String query;
        if (keyValue == null || keyValue.trim().isEmpty()) {
            query = "SELECT COUNT(*) FROM b2_hoadonchitiet";
        } else {
            query = "SELECT COUNT(*) FROM b2_hoadonchitiet WHERE " + keyColumn + " = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (keyValue != null && !keyValue.trim().isEmpty()) {
                pstmt.setString(1, keyValue);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        System.err.println("Không tìm thấy dữ liệu với " + keyColumn + " = " + keyValue);
                        return false;
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL khi kiểm tra dữ liệu: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Xác định loại chi phí dựa trên các mã tham chiếu.
     *
     * @param maSDDV Mã sử dụng dịch vụ
     * @param maDatPhong Mã đặt phòng
     * @param maKiemTra Mã kiểm tra phòng
     * @return Loại chi phí ("Dịch vụ", "Phòng", "Đền bù", hoặc "Khác")
     */
    private String determineLoaiChiPhi(String maSDDV, String maDatPhong, String maKiemTra) {
        if (maSDDV != null && !maSDDV.isEmpty()) {
            return "Dịch vụ";
        } else if (maDatPhong != null && !maDatPhong.isEmpty()) {
            return "Phòng";
        } else if (maKiemTra != null && !maKiemTra.isEmpty()) {
            return "Đền bù";
        } else {
            return "Khác";
        }
    }

    /**
     * Hiển thị thông báo lỗi.
     *
     * @param message Thông điệp lỗi
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
}