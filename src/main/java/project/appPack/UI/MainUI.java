package project.appPack.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import project.appPack.UI.MainRegion.MainPanel;
import project.appPack.UI.MenuRegion.MenuPanel;
import project.appPack.UI.MenuRegion.MenuScrollPane;

public class MainUI extends JFrame {

    public MainUI() {
        setTitle("Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#FFFFFF"));

        MainPanel mainPanel = new MainPanel();
        add(mainPanel, BorderLayout.CENTER);

        MenuPanel menuPanel = new MenuPanel(mainPanel);
        MenuScrollPane scrollPanel = new MenuScrollPane(menuPanel);
        scrollPanel.setPreferredSize(new Dimension(160, 720));
        add(scrollPanel, BorderLayout.WEST);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
