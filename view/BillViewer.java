package view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BillViewer extends JDialog {
    private JTable table;
    private JLabel lblMaHD, lblKhachHang, lblNgayLap, lblTongTien;
    private JButton btnIn, btnDong;

    public BillViewer(Frame parent, Map<String, String> hoaDonInfo, List<Map<String, String>> chiTietHoaDon) {
        super(parent, "Chi tiết hóa đơn", true);
        setSize(750, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("CHI TIẾT HÓA ĐƠN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panel thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 15, 5));
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

        infoPanel.add(new JLabel("Mã hóa đơn:", SwingConstants.RIGHT));
        infoPanel.add(lblMaHD);
        infoPanel.add(new JLabel("Khách hàng:", SwingConstants.RIGHT));
        infoPanel.add(lblKhachHang);
        infoPanel.add(new JLabel("Ngày lập:", SwingConstants.RIGHT));
        infoPanel.add(lblNgayLap);
        infoPanel.add(new JLabel("Tổng tiền:", SwingConstants.RIGHT));
        infoPanel.add(lblTongTien);

        add(infoPanel, BorderLayout.WEST);

        // Bảng chi tiết món
        String[] columnNames = {"Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tắt chỉnh sửa ô
            }
        };

        for (Map<String, String> row : chiTietHoaDon) {
            model.addRow(new Object[]{
                row.getOrDefault("tenmon", "N/A"),
                row.getOrDefault("soluong", "0"),
                row.getOrDefault("dongia", "0"),
                row.getOrDefault("thanhtien", "0")
            });
        }

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);

        // Căn giữa tiêu đề cột
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        // Căn giữa nội dung cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        btnIn = new JButton("In hóa đơn");
        btnDong = new JButton("Đóng");

        btnIn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnDong.addActionListener(e -> dispose());
        btnIn.addActionListener(e -> inHoaDon());

        buttonPanel.add(btnIn);
        buttonPanel.add(btnDong);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void inHoaDon() {
        try {
            table.print(); // In bảng trực tiếp
            JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu in hóa đơn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Test giao diện
    public static void main(String[] args) {
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

        BillViewer billViewer = new BillViewer(null, hoaDonInfo, chiTietHoaDon);
        billViewer.setVisible(true);
    }
    
}