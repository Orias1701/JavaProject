package view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BillView extends JDialog {

    private JTable table;
    private JLabel lblMaHD, lblKhachHang, lblNgayLap, lblTongTien;
    private JButton btnIn, btnDong;

    public BillView(Frame parent, Map<String, String> hoaDonInfo, List<Map<String, String>> chiTietHoaDon) {
        super(parent, "Chi tiết hóa đơn", true);
        setSize(700, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Panel trên cùng - thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        infoPanel.setBackground(Color.WHITE);

        lblMaHD = new JLabel("Mã hóa đơn: " + hoaDonInfo.getOrDefault("mahoadon", "N/A"));
        lblKhachHang = new JLabel("Khách hàng: " + hoaDonInfo.getOrDefault("tenkhach", "N/A"));
        lblNgayLap = new JLabel("Ngày lập: " + hoaDonInfo.getOrDefault("ngaylap", "N/A"));
        lblTongTien = new JLabel("Tổng tiền: " + hoaDonInfo.getOrDefault("tongtien", "0") + " VNĐ");

        lblMaHD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNgayLap.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));

        infoPanel.add(lblMaHD);
        infoPanel.add(lblKhachHang);
        infoPanel.add(lblNgayLap);
        infoPanel.add(lblTongTien);

        add(infoPanel, BorderLayout.NORTH);

        // Bảng chi tiết món
        String[] columnNames = {"Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        Object[][] tableData = new Object[chiTietHoaDon.size()][4];

        for (int i = 0; i < chiTietHoaDon.size(); i++) {
            Map<String, String> row = chiTietHoaDon.get(i);
            tableData[i][0] = row.getOrDefault("tenmon", "");
            tableData[i][1] = row.getOrDefault("soluong", "");
            tableData[i][2] = row.getOrDefault("dongia", "");
            tableData[i][3] = row.getOrDefault("thanhtien", "");
        }

        table = new JTable(tableData, columnNames);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        btnIn = new JButton("In hóa đơn");
        btnDong = new JButton("Đóng");

        btnDong.addActionListener(e -> dispose());
        // btnIn.addActionListener(e -> inHoaDon());

        buttonPanel.add(btnIn);
        buttonPanel.add(btnDong);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Bạn có thể thêm phương thức in ở đây nếu cần
    // private void inHoaDon() { ... }
    public static void main(String[] args) {
        // Test the BillView
        Map<String, String> hoaDonInfo = Map.of(
                "mahoadon", "HD001",
                "tenkhach", "Nguyen Van A",
                "ngaylap", "2023-10-01",
                "tongtien", "500000"
        );

        List<Map<String, String>> chiTietHoaDon = List.of(
                Map.of("tenmon", "Món 1", "soluong", "2", "dongia", "100000", "thanhtien", "200000"),
                Map.of("tenmon", "Món 2", "soluong", "1", "dongia", "300000", "thanhtien", "300000")
        );

        BillView billView = new BillView(null, hoaDonInfo, chiTietHoaDon);
        billView.setVisible(true);
    }
}
