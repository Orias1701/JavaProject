package view;

import controller.MainController;
import controller.SimpleServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import view.FooterRegion.FooterPanel;
import view.HeaderRegion.HeaderPanel;
import view.MainRegion.ContentPanel;
import view.MenuRegion.MenuPanel;

public class MainUI extends JFrame {

    public MainUI() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Style.WIN_WIDTH, 810);
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#FFFFFF"));

        SimpleServer.start();

        add(new LoginPanel(this));

        setVisible(true);
    }

    public void showMainInterface() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Khởi tạo các vùng
        HeaderPanel headerPanel = new HeaderPanel();
        FooterPanel footerPanel = new FooterPanel();
        MenuPanel menuPanel = new MenuPanel();
        ContentPanel contentPanel = new ContentPanel();

        // Thêm vào layout
        add(headerPanel, BorderLayout.NORTH);
        add(footerPanel, BorderLayout.SOUTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Khởi tạo controller
        new MainController(contentPanel, menuPanel);

        revalidate();
        repaint();
    }
}