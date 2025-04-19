package view.HeaderRegion;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import controller.UserSession; // Giả sử có class này để xử lý đăng xuất

public class HeaderPanel extends javax.swing.JPanel {
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

        // Label "USER" sát trái hình tròn
        JLabel userLabel = new JLabel("USER");
        userLabel.setFont(Style.SC_S);
        userLabel.setForeground(Style.MAIN_CL);
        userLabel.setPreferredSize(new Dimension(150, 70));
        userLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
        
                g2d.setColor(Style.GRAY_CL);

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
            try {
                UserSession.logOut(); // Giả sử có phương thức này
                javax.swing.JOptionPane.showMessageDialog(this, "Đăng xuất thành công", "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                // Có thể chuyển hướng đến màn hình đăng nhập
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi đăng xuất: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(10, 5, 10, 15);
        add(logoutButton, gbc);
    }
}