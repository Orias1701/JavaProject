package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import controller.LogHandler;
import controller.MainCtrl;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import view.FooterRegion.FooterPanel;
import view.HeaderRegion.HeaderPanel;
import view.MainRegion.ContentPanel;
import view.MenuRegion.MenuPanel;

public class MainUI extends JFrame {

    private ContentPanel contentPanel;

    public MainUI() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Style.WIN_WIDTH, 810);
        setMinimumSize(new Dimension(1280, 720));
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#FFFFFF"));

        LogHandler.logInfo("MainUI constructor");
        MainCtrl.startServer();

        add(new LoginPanel(this));

        setVisible(true);
    }

    public void showMainInterface() {
        LogHandler.logInfo("Show main interface");
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        HeaderPanel headerPanel = new HeaderPanel();
        FooterPanel footerPanel = new FooterPanel();
        MenuPanel menuPanel = new MenuPanel();
        contentPanel = new ContentPanel();

        menuPanel.setTableSelectionListener((tableName, tableComment) -> {
            try {
                TableDataResult result = ApiClient.getTableData(tableName);
                if (result.data != null && !result.data.isEmpty()) {
                    contentPanel.updateTableData(
                        result.data,
                        result.columnComments,
                        result.keyColumn,
                        tableName,
                        tableComment
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Không có dữ liệu cho bảng " + tableComment,
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE
                    );
                    contentPanel.updateTableData(null, null, null, tableName, tableComment);
                }
            } catch (Exception ex) {
                LogHandler.logError("Lỗi khi tải dữ liệu bảng: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi tải dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Thiết lập HomeSelectionListener
        menuPanel.setHomeSelectionListener(() -> {
            contentPanel.showHomePanel();
            LogHandler.logInfo("Hiển thị HomePanel trong ContentPanel");
        });

        add(headerPanel, BorderLayout.NORTH);
        add(footerPanel, BorderLayout.SOUTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        new MainCtrl(contentPanel, menuPanel);

        // Làm mới danh sách bảng
        menuPanel.refreshTableList();

        // Hiển thị HomePanel mặc định khi khởi động
        contentPanel.showHomePanel();

        revalidate();
        repaint();
    }
}