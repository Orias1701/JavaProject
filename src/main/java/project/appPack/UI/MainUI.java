package project.appPack.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainUI extends JFrame {

    public MainUI() {
        setTitle("Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#FFFFFF"));

        // Khu vực chính
        MainContentPanel mainContentPanel = new MainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        // Menu trái
        MenuPanel menuPanel = new MenuPanel(mainContentPanel);
        MenuScrollPane scrollPanel = new MenuScrollPane(menuPanel);
        scrollPanel.setPreferredSize(new Dimension(160, 720));
        add(scrollPanel, BorderLayout.WEST);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
