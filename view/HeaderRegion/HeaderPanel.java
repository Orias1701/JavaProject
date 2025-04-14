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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HeaderPanel extends javax.swing.JPanel {
    public HeaderPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(Style.WIN_WIDTH, 70));
        setBorder(BorderFactory.createMatteBorder(0, 0, 7, 0, Style.ACT_CL));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

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

        // Hình tròn 50x50 bên phải
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
        gbc.insets = new java.awt.Insets(10, 5, 10, 20);
        add(circlePanel, gbc);

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

        circlePanel.repaint();
    }
}