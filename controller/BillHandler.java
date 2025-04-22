package controller;

public class BillHandler {

    // private final CheckBill checkBill = new CheckBill();
    // private  BillView view ;

    // /**
    //  * Hàm gọi giao diện chi tiết hóa đơn theo mã hóa đơn.
    //  * 
    //  * @param parent JFrame cha
    //  * @param maHoaDon Mã hóa đơn
    //  */
    // public void showInvoiceDetail(Frame parent, String maHoaDon) {
    //     try {
    //         // 1. Lấy danh sách mã chi tiết từ hóa đơn
    //         List<String> danhSachMaChiTiet = getListChiTiet(maHoaDon);

    //         // 2. Tạo danh sách dữ liệu chi tiết món
    //         List<Map<String, String>> chiTietHoaDon = new ArrayList<>();

    //         for (String maChiTiet : danhSachMaChiTiet) {
    //             Map<String, Object> data = checkBill.getInvoiceDetail(maChiTiet);
    //             if ((boolean) data.getOrDefault("success", false)) {
    //                 String tenMon = data.getOrDefault("TenDichVu", "").toString();
    //                 if (tenMon.isBlank()) {
    //                     tenMon = data.getOrDefault("TenThietBi", "").toString();
    //                 }
    //                 if (tenMon.isBlank()) {
    //                     tenMon = "Phụ phí phòng " + data.getOrDefault("MaPhong", "");
    //                 }

    //                 Map<String, String> row = new HashMap<>();
    //                 row.put("tenmon", tenMon);
    //                 row.put("soluong", String.valueOf(data.get("SoLuong")));
    //                 row.put("dongia", String.valueOf(data.get("DonGia")));
    //                 row.put("thanhtien", String.valueOf(data.get("ThanhTien")));
    //                 chiTietHoaDon.add(row);
    //             }
    //         }

    //         // 3. Giả sử gọi lấy thông tin tổng quan hóa đơn
    //         Map<String, String> hoaDonInfo = getThongTinHoaDon(maHoaDon);

    //         // 4. Gọi giao diện view
    //         BillView billView = new BillView(parent, hoaDonInfo, chiTietHoaDon);
    //         view.setVisible(true);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(parent, "Lỗi khi xử lý hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    //     }
    // }

    // /**
    //  * Hàm mô phỏng truy vấn lấy danh sách mã chi tiết từ bảng b2_hoadonchitiet.
    //  * Bạn có thể thay bằng JDBC thật.
    //  */
    // private List<String> getListChiTiet(String maHoaDon) throws Exception {
    //     List<String> list = new ArrayList<>();
    //     var conn = DatabaseUtil.getConnection();
    //     var query = "SELECT MaChiTiet FROM b2_hoadonchitiet WHERE MaHoaDon = ?";
    //     try (var pstmt = conn.prepareStatement(query)) {
    //         pstmt.setString(1, maHoaDon);
    //         var rs = pstmt.executeQuery();
    //         while (rs.next()) {
    //             list.add(rs.getString("MaChiTiet"));
    //         }
    //     }
    //     return list;
    // }

    // /**
    //  * Hàm mô phỏng lấy thông tin hóa đơn tổng quan. Có thể thay bằng DAO hoặc xử lý thực tế.
    //  */
    // private Map<String, String> getThongTinHoaDon(String maHoaDon) throws Exception {
    //     Map<String, String> data = new HashMap<>();
    //     var conn = DatabaseUtil.getConnection();
    //     var query = """
    //         SELECT hd.MaHoaDon, kh.HoTen AS TenKhach, hd.NgayLap, hd.TongTien
    //         FROM b1_hoadon hd
    //         JOIN a1_khachhang kh ON hd.MaKhach = kh.MaKhach
    //         WHERE hd.MaHoaDon = ?
    //     """;
    //     try (var pstmt = conn.prepareStatement(query)) {
    //         pstmt.setString(1, maHoaDon);
    //         var rs = pstmt.executeQuery();
    //         if (rs.next()) {
    //             data.put("mahoadon", rs.getString("MaHoaDon"));
    //             data.put("tenkhach", rs.getString("TenKhach"));
    //             data.put("ngaylap", rs.getString("NgayLap"));
    //             data.put("tongtien", rs.getString("TongTien"));
    //         }
    //     }
    //     return data;
    // }
     public static void main(String[] args) {
        // // Thiết lập UIManager (nếu bạn đang dùng style đặc biệt)
        // try {
        //     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // } catch (Exception ignored) {}

        // // Tạo Frame ẩn làm cha (nếu cần)
        // JFrame frame = new JFrame();
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(400, 300); // chỉ để hiển thị nếu bạn muốn

        // // Mã hóa đơn cần test – thay bằng mã bạn có trong CSDL
        // String maHoaDon = "HD001"; // ví dụ

        // // Gọi hiển thị chi tiết hóa đơn
        // new BillHandler().showInvoiceDetail(frame, maHoaDon);
    }
}   
