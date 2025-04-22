package view.HeaderRegion;

import view.MainUI;
import view.Style;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
// import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import controller.LogHandler;
import controller.UserSession;
import model.ApiClient;
import java.sql.SQLException;
import java.util.Map;

public class HeaderPanel extends javax.swing.JPanel {
    private JLabel userLabel;

    public HeaderPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(Style.WIN_WIDTH, 70));
        setBorder(BorderFactory.createMatteBorder(0, 0, 7, 0, Style.ACT_CL));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Logo bên trái
        JLabel branchLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/view/HeaderRegion/Branch.png"));
            branchLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100, 50, java.awt.Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            branchLabel.setText("Branch.png not found");
            System.err.println("Error loading Branch.png: " + e.getMessage());
        }
        branchLabel.setPreferredSize(new Dimension(100, 50));
        branchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(10, 20, 10, 0);
        add(branchLabel, gbc);

        // Label hiển thị TenNhanVien
        String username = UserSession.getCurrentUsername();
        String displayName = "Guest";
        if (username != null) {
            try {
                Map<String, String> employeeInfo = ApiClient.getEmployeeInfo(username);
                displayName = employeeInfo.getOrDefault("TenNhanVien", username);
            } catch (SQLException e) {
                LogHandler.logError("Lỗi khi lấy tên nhân viên: " + e.getMessage(), e);
                displayName = username;
            }
        }
        userLabel = new JLabel(displayName);
        userLabel.setFont(Style.SC_S);
        userLabel.setForeground(Style.MAIN_CL);
        userLabel.setPreferredSize(new Dimension(150, 70));
        userLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String currentUsername = UserSession.getCurrentUsername();
                if (currentUsername != null) {
                    try {
                        Map<String, String> employeeInfo = ApiClient.getEmployeeInfo(currentUsername);
                        if (!employeeInfo.isEmpty()) {
                            String info = String.format(
                                "Mã Nhân Viên: %s\nTên Nhân Viên: %s\nNhóm: %s",
                                employeeInfo.getOrDefault("MaNhanVien", "N/A"),
                                employeeInfo.getOrDefault("TenNhanVien", "N/A"),
                                employeeInfo.getOrDefault("Group", "N/A")
                            );
                            JOptionPane.showMessageDialog(
                                HeaderPanel.this,
                                info,
                                "Thông Tin Nhân Viên",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                HeaderPanel.this,
                                "Không tìm thấy thông tin nhân viên.",
                                "Thông Báo",
                                JOptionPane.WARNING_MESSAGE
                            );
                        }
                    } catch (SQLException ex) {
                        LogHandler.logError("Lỗi khi lấy thông tin nhân viên: " + ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(
                            HeaderPanel.this,
                            "Lỗi khi lấy thông tin: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        HeaderPanel.this,
                        "Vui lòng đăng nhập để xem thông tin.",
                        "Thông Báo",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(0, 0, 0, 5);
        add(userLabel, gbc);

        // Hình tròn 50x50
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Style.ACT_CL);
                g2d.setStroke(new java.awt.BasicStroke(3));
                g2d.drawOval(5, 5, 40, 40);
            }
        };
        circlePanel.setPreferredSize(new Dimension(50, 50));
        circlePanel.setMinimumSize(new Dimension(50, 50));
        circlePanel.setMaximumSize(new Dimension(50, 50));
        circlePanel.setOpaque(false);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(10, 5, 10, 5);
        add(circlePanel, gbc);

        // Nút đăng xuất với biểu tượng cánh cửa tự vẽ
        JButton logoutButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Style.ACT_CL);
                int[] xDoors = {30, 10, 10, 30, 25, 15, 15, 25};
                int[] yDoors = {10, 10, 40, 40, 35, 35, 15, 15};
                g2d.fillPolygon(xDoors, yDoors, 8);
                int[] xOuts = {33, 43, 33, 33, 25, 22, 25, 33};
                int[] yOuts = {31, 25, 19, 22, 22, 25, 28, 28};
                g2d.fillPolygon(xOuts, yOuts, 8);
            }
        };
        logoutButton.setPreferredSize(new Dimension(45, 50));
        logoutButton.setMinimumSize(new Dimension(45, 50));
        logoutButton.setMaximumSize(new Dimension(45, 50));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder());
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setToolTipText("Đăng xuất");
        logoutButton.addActionListener(e -> {
            // Kiểm tra nếu không có phiên thì không hiển thị dialog xác nhận
            if (UserSession.getCurrentUsername() == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Bạn chưa đăng nhập.",
                    "Thông Báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // Hiển thị dialog xác nhận
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác Nhận Đăng Xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Lưu tham chiếu đến MainUI trước khi thay đổi giao diện
                    java.awt.Component topLevelAncestor = getTopLevelAncestor();
                    if (topLevelAncestor == null) {
                        LogHandler.logError("Không thể đăng xuất: getTopLevelAncestor trả về null");
                        JOptionPane.showMessageDialog(
                            this,
                            "Lỗi hệ thống: Không thể xác định cửa sổ chính để đăng xuất.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    MainUI mainUI = (MainUI) topLevelAncestor;

                    UserSession.logOut();
                    UserSession.clearSession();
                    ApiClient.clearAuthHeader();

                    // Gọi showMainInterface để làm mới giao diện
                    mainUI.showMainInterface();
                    updateUserLabel();

                    // Hiển thị thông báo sau khi chuyển giao diện
                    JOptionPane.showMessageDialog(
                        mainUI,
                        "Đăng xuất thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    LogHandler.logError("Lỗi khi đăng xuất: " + ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi đăng xuất: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(10, 5, 10, 15);
        add(logoutButton, gbc);
    }

    public void updateUserLabel() {
        String username = UserSession.getCurrentUsername();
        String displayName = "Guest";
        if (username != null) {
            try {
                Map<String, String> employeeInfo = ApiClient.getEmployeeInfo(username);
                displayName = employeeInfo.getOrDefault("TenNhanVien", username);
            } catch (SQLException e) {
                LogHandler.logError("Lỗi khi lấy tên nhân viên: " + e.getMessage(), e);
                displayName = username;
            }
        }
        userLabel.setText(displayName);
    }
}