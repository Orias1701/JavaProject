package controller;

import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import view.BillViewer;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;

public class BillHandler {
    private final CheckBill checkBill;

    public BillHandler(ContentPanel contentPanel, TablePanel tablePanel) {
        this.checkBill = new CheckBill("defaultString", contentPanel, tablePanel);
    }

    public void showInvoiceDetail(Frame parent, String maHoaDon) {
        try {
            // 1. Lấy thông tin hóa đơn (dauhd)
            Map<String, String> hoaDonInfo = getThongTinHoaDon(maHoaDon);

            // 2. Lấy thông tin phòng (hdphong)
            List<Map<String, String>> phongInfo = getPhongInfo(maHoaDon);
            int soLuongPhong = phongInfo.size();

            // 3. Lấy thông tin kiểm tra phòng (ktphong)
            Map<String, Object> kiemTraPhongInfo = getKiemTraPhongInfo(maHoaDon);

            // 4. Lấy thông tin dịch vụ (sddv)
            List<Map<String, String>> dichVuInfo = getDichVuInfo(maHoaDon);
            int soLuongDichVuDaDat = dichVuInfo.stream()
                .mapToInt(row -> Integer.parseInt(row.getOrDefault("soluong", "0")))
                .sum();

            // 5. Hiển thị giao diện BillViewer
            BillViewer billViewer = new BillViewer(parent, hoaDonInfo, phongInfo, soLuongPhong, kiemTraPhongInfo, dichVuInfo, soLuongDichVuDaDat);
            billViewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi khi xử lý hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, String> getThongTinHoaDon(String maHoaDon) throws Exception {
        Map<String, String> data = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                SELECT hd.MaHoaDon, nv.HoTen AS TenNhanVien, hd.NgayLap, hd.TongTien
                FROM b1_hoadon hd
                JOIN a2_nhanvien nv ON hd.MaNhanVien = nv.MaNhanVien
                WHERE hd.MaHoaDon = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maHoaDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        data.put("mahoadon", rs.getString("MaHoaDon"));
                        data.put("tennhanvien", rs.getString("TenNhanVien"));
                        data.put("ngaylap", rs.getString("NgayLap"));
                        data.put("tongtien", rs.getString("TongTien"));
                    }
                }
            }
        }
        return data;
    }

    private List<Map<String, String>> getPhongInfo(String maHoaDon) throws Exception {
        List<Map<String, String>> phongInfo = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                SELECT 
                    hdc.MaPhong, 
                    lp.TenLoaiPhong, 
                    lp.GiaLoaiPhong, 
                    dp.NgayNhan, 
                    dp.NgayTra, 
                    dp.NgayHen, 
                    dp.Phat
                FROM b2_hoadonchitiet hdc
                JOIN c1_datphong dp ON hdc.MaDatPhong = dp.MaDatPhong
                JOIN c2_phong p ON hdc.MaPhong = p.MaPhong
                JOIN c3_loaiphong lp ON p.MaLoaiPhong = lp.MaLoaiPhong
                WHERE hdc.MaHoaDon = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maHoaDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("maphong", rs.getString("MaPhong"));
                        row.put("tenloaiphong", rs.getString("TenLoaiPhong"));
                        String giaPhong = rs.getString("GiaLoaiPhong");
                        row.put("giaphong", giaPhong);

                        String ngayNhan = rs.getString("NgayNhan");
                        String ngayTra = rs.getString("NgayTra");
                        String ngayHen = rs.getString("NgayHen");
                        row.put("ngaynhan", ngayNhan);
                        row.put("ngaytra", ngayTra);
                        row.put("ngayhen", ngayHen);

                        // Tính tiền phòng: giá phòng * (ngày trả - ngày nhận)
                        long days = java.time.temporal.ChronoUnit.DAYS.between(
                            java.time.LocalDate.parse(ngayNhan.split(" ")[0]),
                            java.time.LocalDate.parse(ngayTra.split(" ")[0])
                        );
                        long tienPhong = Long.parseLong(giaPhong) * days;
                        row.put("tienphong", String.valueOf(tienPhong));

                        String tienPhat = rs.getString("Phat");
                        row.put("tienphat", tienPhat);

                        long tongTienPhong = tienPhong + Long.parseLong(tienPhat);
                        row.put("tongtienphong", String.valueOf(tongTienPhong));

                        phongInfo.add(row);
                    }
                }
            }
        }
        return phongInfo;
    }

    private Map<String, Object> getKiemTraPhongInfo(String maHoaDon) throws Exception {
        Map<String, Object> kiemTraInfo = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Kiểm tra tiền đền bù
            String queryDenBu = """
                SELECT kt.TienDenBu
                FROM b2_hoadonchitiet hdc
                JOIN d3_kiemtraphong kt ON hdc.MaKiemTra = kt.MaKiemTra
                WHERE hdc.MaHoaDon = ?
            """;
            long tienDenBu = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(queryDenBu)) {
                pstmt.setString(1, maHoaDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tienDenBu = rs.getLong("TienDenBu");
                    }
                }
            }
            kiemTraInfo.put("tiendenbu", String.valueOf(tienDenBu));

            // Nếu tiền đền bù != 0, lấy chi tiết thiết bị hỏng
            if (tienDenBu != 0) {
                List<Map<String, String>> thietBiHong = new ArrayList<>();
                String queryChiTiet = """
                    SELECT 
                        tb.TenThietBi, 
                        tb.TienDenBu, 
                        ktct.SoLuongHong
                    FROM b2_hoadonchitiet hdc
                    JOIN d3_kiemtraphong kt ON hdc.MaKiemTra = kt.MaKiemTra
                    JOIN d4_kiemtrachitiet ktct ON kt.MaKiemTra = ktct.MaKiemTra
                    JOIN d2_thietbi tb ON ktct.MaThietBi = tb.MaThietBi
                    WHERE hdc.MaHoaDon = ?
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(queryChiTiet)) {
                    pstmt.setString(1, maHoaDon);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            row.put("tenthietbi", rs.getString("TenThietBi"));
                            String tienDen = rs.getString("TienDenBu");
                            row.put("tienden", tienDen);
                            String soLuongHong = rs.getString("SoLuongHong");
                            row.put("soluonghong", soLuongHong);
                            long tongTienDen = Long.parseLong(tienDen) * Long.parseLong(soLuongHong);
                            row.put("tongtienden", String.valueOf(tongTienDen));
                            thietBiHong.add(row);
                        }
                    }
                }
                kiemTraInfo.put("thietbihong", thietBiHong);
            } else {
                kiemTraInfo.put("thietbihong", new ArrayList<Map<String, String>>());
            }
        }
        return kiemTraInfo;
    }

    private List<Map<String, String>> getDichVuInfo(String maHoaDon) throws Exception {
        List<Map<String, String>> dichVuInfo = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                SELECT 
                    dv.TenDichVu, 
                    dv.TienDichVu, 
                    sddvct.SoLuong
                FROM b2_hoadonchitiet hdc
                JOIN e2_sddv sddv ON hdc.MaSDDV = sddv.MaSDDV
                JOIN e3_sddvchitiet sddvct ON sddv.MaSDDV = sddvct.MaSDDV
                JOIN e1_dichvu dv ON sddvct.MaDichVu = dv.MaDichVu
                WHERE hdc.MaHoaDon = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maHoaDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("tendichvu", rs.getString("TenDichVu"));
                        String tienDichVu = rs.getString("TienDichVu");
                        row.put("tiendichvu", tienDichVu);
                        String soLuong = rs.getString("SoLuong");
                        row.put("soluong", soLuong);
                        long tongTienDichVu = Long.parseLong(tienDichVu) * Long.parseLong(soLuong);
                        row.put("tongtiendichvu", String.valueOf(tongTienDichVu));
                        dichVuInfo.add(row);
                    }
                }
            }
        }
        return dichVuInfo;
    }
}