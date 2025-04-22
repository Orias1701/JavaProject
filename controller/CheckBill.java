package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class CheckBill {

    /**
     * Lấy chi tiết hóa đơn dựa trên MaChiTiet từ bảng b2_hoadonchitiet.
     * Trả về Map chứa thông tin chi tiết, bao gồm dữ liệu bổ sung từ các bảng liên quan.
     *
     * @param maChiTiet Mã chi tiết hóa đơn (MaChiTiet)
     * @return Map<String, Object> chứa dữ liệu chi tiết hoặc thông báo lỗi
     */
    public Map<String, Object> getInvoiceDetail(String maChiTiet) throws Exception {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                SELECT 
                    hdc.MaChiTiet, hdc.MaHoaDon, hdc.LoaiChiPhi, hdc.MaThamChieu, hdc.MoTa, 
                    hdc.SoLuong, hdc.DonGia, hdc.ThanhTien, hdc.MaSDDV, hdc.MaKT, hdc.MaDatPhong,
                    COALESCE(dv.TenDichVu, '') AS TenDichVu,
                    COALESCE(tb.TenThietBi, '') AS TenThietBi,
                    COALESCE(dp.MaPhong, '') AS MaPhong
                FROM b2_hoadonchitiet hdc
                LEFT JOIN e2_sddv sddv ON hdc.MaSDDV = sddv.MaSDDV
                LEFT JOIN e3_chitietsddv ctdv ON sddv.MaSDDV = ctdv.MaSDDV
                LEFT JOIN e1_dichvu dv ON ctdv.MaDichVu = dv.MaDichVu
                LEFT JOIN d3_kiemtraphong kt ON hdc.MaKT = kt.MaKiemTra
                LEFT JOIN d4_kiemtrachitiet ktc ON kt.MaKiemTra = ktc.MaKiemTra
                LEFT JOIN d1_thietbi tb ON ktc.MaThietBi = tb.MaThietBi
                LEFT JOIN c3_datphong dp ON hdc.MaDatPhong = dp.MaDatPhong
                WHERE hdc.MaChiTiet = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maChiTiet);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    response.put("MaChiTiet", rs.getString("MaChiTiet"));
                    response.put("MaHoaDon", rs.getString("MaHoaDon"));
                    response.put("LoaiChiPhi", rs.getString("LoaiChiPhi"));
                    response.put("MaThamChieu", rs.getString("MaThamChieu"));
                    response.put("MoTa", rs.getString("MoTa"));
                    response.put("SoLuong", rs.getInt("SoLuong"));
                    response.put("DonGia", rs.getDouble("DonGia"));
                    response.put("ThanhTien", rs.getDouble("ThanhTien"));
                    response.put("MaSDDV", rs.getString("MaSDDV"));
                    response.put("MaKT", rs.getString("MaKT"));
                    response.put("MaDatPhong", rs.getString("MaDatPhong"));
                    response.put("TenDichVu", rs.getString("TenDichVu"));
                    response.put("TenThietBi", rs.getString("TenThietBi"));
                    response.put("MaPhong", rs.getString("MaPhong"));
                    response.put("success", true);
                } else {
                    response.put("success", false);
                    response.put("message", "Không tìm thấy chi tiết hóa đơn với MaChiTiet: " + maChiTiet);
                    JOptionPane.showMessageDialog(null, "Không tìm thấy chi tiết hóa đơn với MaChiTiet: " + maChiTiet, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi truy vấn dữ liệu: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return response;
    }

    /**
     * Lấy chi tiết hóa đơn đơn giản (chỉ từ b2_hoadonchitiet, không join bảng khác).
     * Dùng khi cần hiệu suất cao.
     *
     * @param maChiTiet Mã chi tiết hóa đơn (MaChiTiet)
     * @return Map<String, Object> chứa dữ liệu chi tiết hoặc thông báo lỗi
     */
    public Map<String, Object> getSimpleInvoiceDetail(String maChiTiet) throws Exception {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                SELECT MaChiTiet, MaHoaDon, LoaiChiPhi, MaThamChieu, MoTa, SoLuong, 
                       DonGia, ThanhTien, MaSDDV, MaKT, MaDatPhong
                FROM b2_hoadonchitiet
                WHERE MaChiTiet = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maChiTiet);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    response.put("MaChiTiet", rs.getString("MaChiTiet"));
                    response.put("MaHoaDon", rs.getString("MaHoaDon"));
                    response.put("LoaiChiPhi", rs.getString("LoaiChiPhi"));
                    response.put("MaThamChieu", rs.getString("MaThamChieu"));
                    response.put("MoTa", rs.getString("MoTa"));
                    response.put("SoLuong", rs.getInt("SoLuong"));
                    response.put("DonGia", rs.getDouble("DonGia"));
                    response.put("ThanhTien", rs.getDouble("ThanhTien"));
                    response.put("MaSDDV", rs.getString("MaSDDV"));
                    response.put("MaKT", rs.getString("MaKT"));
                    response.put("MaDatPhong", rs.getString("MaDatPhong"));
                    response.put("success", true);
                } else {
                    response.put("success", false);
                    response.put("message", "Không tìm thấy chi tiết hóa đơn với MaChiTiet: " + maChiTiet);
                    JOptionPane.showMessageDialog(null, "Không tìm thấy chi tiết hóa đơn với MaChiTiet: " + maChiTiet, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi truy vấn dữ liệu: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi khi truy vấn dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return response;
    }
}