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

        JPanel kiemTraPanel = new JPanel(new BorderLayout());
        kiemTraPanel.setBackground(Color.WHITE);
        String tienDenBu = (String) kiemTraPhongInfo.get("tiendenbu");
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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin hóa đơn", hoaDonPanel);
        tabbedPane.addTab("Phòng đã đặt", phongPanel);
        tabbedPane.addTab("Kiểm tra phòng", kiemTraPanel);
        tabbedPane.addTab("Dịch vụ sử dụng", dichVuPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
}