package view;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    private Image backgroundImage;

    public HomePanel() {
        setLayout(null);
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL imgURL = getClass().getResource("Manager.jpg");
            if (imgURL == null) {
                throw new IllegalArgumentException("Không tìm thấy file ảnh: images/Manager.jpg");
            }
            backgroundImage = new ImageIcon(imgURL).getImage();
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + e.getMessage());
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         JFrame frame = new JFrame("Home Panel Example");
    //         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //         frame.setPreferredSize(new Dimension(950, 610));
    //         frame.setMinimumSize(new Dimension(950, 610));
    //         frame.setLocationRelativeTo(null);
    //         frame.setContentPane(new HomePanel());
    //         frame.pack();
    //         frame.setVisible(true);
    //     });
    // }
}