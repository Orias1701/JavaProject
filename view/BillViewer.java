package view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BillViewer extends JFrame {
    public BillViewer(Frame parent, Map<String, String> hoaDonInfo, List<Map<String, String>> phongInfo, int soLuongPhong,
                     Map<String, Object> kiemTraPhongInfo, List<Map<String, String>> dichVuInfo, int soLuongDichVuDaDat) {
        super("Chi tiết hóa đơn");
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Phần thông tin hóa đơn (dauhd)
        JPanel hoaDonPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        hoaDonPanel.setBackground(Color.WHITE);
        hoaDonPanel.add(new JLabel("Mã hóa đơn:"));
        hoaDonPanel.add(new JLabel(hoaDonInfo.getOrDefault("mahoadon", "")));
        hoaDonPanel.add(new JLabel("Nhân viên:"));
        hoaDonPanel.add(new JLabel(hoaDonInfo.getOrDefault("tennhanvien", "")));
        hoaDonPanel.add(new JLabel("Ngày lập:"));
        hoaDonPanel.add(new JLabel(hoaDonInfo.getOrDefault("ngaylap", "")));
        hoaDonPanel.add(new JLabel("Tổng tiền:"));
        hoaDonPanel.add(new JLabel(hoaDonInfo.getOrDefault("tongtien", "")));

        // Phần thông tin phòng (hdphong)
        JPanel phongPanel = new JPanel(new BorderLayout());
        phongPanel.setBackground(Color.WHITE);
        JLabel phongLabel = new JLabel("Số lượng phòng đã đặt: " + soLuongPhong);
        phongPanel.add(phongLabel, BorderLayout.NORTH);

        DefaultTableModel phongModel = new DefaultTableModel(
            new String[]{"Mã phòng", "Loại phòng", "Giá phòng", "Ngày nhận", "Ngày trả", "Ngày hẹn", "Tiền phòng", "Tiền phạt", "Tổng tiền"},
            0
        );
        JTable phongTable = new JTable(phongModel);
        phongTable.setRowHeight(30);
        for (Map<String, String> row : phongInfo) {
            phongModel.addRow(new Object[]{
                row.get("maphong"),
                row.get("tenloaiphong"),
                row.get("giaphong"),
                row.get("ngaynhan"),
                row.get("ngaytra"),
                row.get("ngayhen"),
                row.get("tienphong"),
                row.get("tienphat"),
                row.get("tongtienphong")
            });
        }
        phongPanel.add(new JScrollPane(phongTable), BorderLayout.CENTER);

        // Phần kiểm tra phòng (ktphong)
        JPanel kiemTraPanel = new JPanel(new BorderLayout());
        kiemTraPanel.setBackground(Color.WHITE);
       // Đoạn cần sửa (dòng 63 trong class gốc)
String tienDenBu = String.valueOf(kiemTraPhongInfo.get("tiendenbu"));

        JLabel kiemTraLabel = new JLabel("Tiền đền bù: " + tienDenBu);
        kiemTraPanel.add(kiemTraLabel, BorderLayout.NORTH);

        if (!tienDenBu.equals("0")) {
            DefaultTableModel thietBiModel = new DefaultTableModel(
                new String[]{"Tên thiết bị", "Tiền đền", "Số lượng hỏng", "Tổng tiền"},
                0
            );
            JTable thietBiTable = new JTable(thietBiModel);
            thietBiTable.setRowHeight(30);
            @SuppressWarnings("unchecked")
            List<Map<String, String>> thietBiHong = (List<Map<String, String>>) kiemTraPhongInfo.get("thietbihong");
            for (Map<String, String> row : thietBiHong) {
                thietBiModel.addRow(new Object[]{
                    row.get("tenthietbi"),
                    row.get("tienden"),
                    row.get("soluonghong"),
                    row.get("tongtienden")
                });
            }
            kiemTraPanel.add(new JScrollPane(thietBiTable), BorderLayout.CENTER);
        }

        // Phần dịch vụ (sddv)
        JPanel dichVuPanel = new JPanel(new BorderLayout());
        dichVuPanel.setBackground(Color.WHITE);
        JLabel dichVuLabel = new JLabel("Số lượng dịch vụ đã đặt: " + soLuongDichVuDaDat);
        dichVuPanel.add(dichVuLabel, BorderLayout.NORTH);

        DefaultTableModel dichVuModel = new DefaultTableModel(
            new String[]{"Tên dịch vụ", "Tiền dịch vụ", "Số lượng", "Tổng tiền"},
            0
        );
        JTable dichVuTable = new JTable(dichVuModel);
        dichVuTable.setRowHeight(30);
        for (Map<String, String> row : dichVuInfo) {
            dichVuModel.addRow(new Object[]{
                row.get("tendichvu"),
                row.get("tiendichvu"),
                row.get("soluong"),
                row.get("tongtiendichvu")
            });
        }
        dichVuPanel.add(new JScrollPane(dichVuTable), BorderLayout.CENTER);

        // Thêm các panel vào tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin hóa đơn", hoaDonPanel);
        tabbedPane.addTab("Phòng đã đặt", phongPanel);
        tabbedPane.addTab("Kiểm tra phòng", kiemTraPanel);
        tabbedPane.addTab("Dịch vụ sử dụng", dichVuPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    public static void main(String[] args) {
        // Test the BillViewer class
        Map<String, String> hoaDonInfo = Map.of(
            "mahoadon", "HD001",
            "tennhanvien", "Nguyen Van A",
            "ngaylap", "2023-10-01",
            "tongtien", "1000000"
        );

        List<Map<String, String>> phongInfo = List.of(
            Map.of("maphong", "P001", "tenloaiphong", "Phòng đơn", "giaphong", "500000", "ngaynhan", "2023-10-01", "ngaytra", "2023-10-05", "ngayhen", "2023-10-06", "tienphong", "2000000", "tienphat", "0", "tongtienphong", "2000000")
        );

        int soLuongPhong = 1;

        Map<String, Object> kiemTraPhongInfo = Map.of(
            "tiendenbu", 50000,
            "thietbihong", List.of(Map.of("tenthietbi", "Điều hòa", "tienden", 50000, "soluonghong", 1, "tongtienden", 50000))
        );

        List<Map<String, String>> dichVuInfo = List.of(
            (Map<String, String>) Map.of(
                "tendichvu", "Giặt là",
                "tiendichvu", String.valueOf(100000),
                "soluong", String.valueOf(2),
                "tongtiendichvu", String.valueOf(200000)
            )
        );

        int soLuongDichVuDaDat = 1;

        BillViewer billViewer = new BillViewer(null, hoaDonInfo, phongInfo, soLuongPhong, kiemTraPhongInfo, dichVuInfo, soLuongDichVuDaDat);
        billViewer.setVisible(true);
    }
}