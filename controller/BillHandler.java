package controller;

import java.awt.Frame;
import java.util.*;
import javax.swing.JOptionPane;
import view.BillViewer;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;

public class BillHandler {

    private final CheckBill checkBill = new CheckBill("defaultString", new ContentPanel(), new TablePanel(new ContentPanel()));

    /**
     * Hiển thị giao diện chi tiết hóa đơn.
     * @param parent JFrame cha
     * @param maHoaDon Mã hóa đơn
     */

    public void showInvoiceDetail(Frame parent, String maHoaDon) {
        try {
            // 1. Lấy danh sách mã chi tiết
            List<String> danhSachMaChiTiet = getListChiTiet(maHoaDon);

            // 2. Tạo danh sách chi tiết hóa đơn
            List<Map<String, String>> chiTietHoaDon = new ArrayList<>();
            for (String maChiTiet : danhSachMaChiTiet) {
                Map<String, Object> data = checkBill.getInvoiceDetail(maChiTiet);
                if ((boolean) data.getOrDefault("success", false)) {
                    String tenMon = data.getOrDefault("TenDichVu", "").toString();
                    if (tenMon.isBlank()) {
                        tenMon = data.getOrDefault("TenThietBi", "").toString();
                    }
                    if (tenMon.isBlank()) {
                        tenMon = "Phụ phí phòng " + data.getOrDefault("MaPhong", "");
                    }

                    Map<String, String> row = new HashMap<>();
                    row.put("tenmon", tenMon);
                    row.put("soluong", String.valueOf(data.getOrDefault("SoLuong", "0")));
                    row.put("dongia", String.valueOf(data.getOrDefault("DonGia", "0")));
                    row.put("thanhtien", String.valueOf(data.getOrDefault("ThanhTien", "0")));
                    chiTietHoaDon.add(row);
                }
            }

            // 3. Lấy thông tin hóa đơn
            Map<String, String> hoaDonInfo = getThongTinHoaDon(maHoaDon);

            // 4. Hiển thị giao diện mới (BillViewer thay vì BillView)
            BillViewer billViewer = new BillViewer(parent, hoaDonInfo, chiTietHoaDon);
            billViewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi khi xử lý hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy danh sách mã chi tiết từ bảng b2_hoadonchitiet.
     */
    private List<String> getListChiTiet(String maHoaDon) throws Exception {
        List<String> list = new ArrayList<>();
        var conn = DatabaseUtil.getConnection();
        var query = "SELECT MaChiTiet FROM b2_hoadonchitiet WHERE MaHoaDon = ?";
        try (var pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maHoaDon);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("MaChiTiet"));
            }
        }
        return list;
    }

    /**
     * Lấy thông tin tổng quan hóa đơn.
     */
    private Map<String, String> getThongTinHoaDon(String maHoaDon) throws Exception {
        Map<String, String> data = new HashMap<>();
        var conn = DatabaseUtil.getConnection();
        var query = """
            SELECT hd.MaHoaDon, kh.HoTen AS TenKhach, hd.NgayLap, hd.TongTien
            FROM b1_hoadon hd
            JOIN a1_khachhang kh ON hd.MaKhach = kh.MaKhach
            WHERE hd.MaHoaDon = ?
        """;
        try (var pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maHoaDon);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                data.put("mahoadon", rs.getString("MaHoaDon"));
                data.put("tenkhach", rs.getString("TenKhach"));
                data.put("ngaylap", rs.getString("NgayLap"));
                data.put("tongtien", rs.getString("TongTien"));
            }
        }
        return data;
    }
}