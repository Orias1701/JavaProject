package controller;

import com.sun.net.httpserver.HttpServer;
import view.MainRegion.ContentPanel;
import view.MenuRegion.MenuPanel;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import java.io.IOException;
import java.net.InetSocketAddress;


public class MainCtrl {
    private final ContentPanel contentPanel;
    private final MenuPanel menuPanel;

    public MainCtrl(ContentPanel contentPanel, MenuPanel menuPanel) {
        if (contentPanel == null || menuPanel == null) {
            throw new IllegalArgumentException("ContentPanel and MenuPanel must not be null");
        }
        this.contentPanel = contentPanel;
        this.menuPanel = menuPanel;

        menuPanel.setTableSelectionListener((tableName, tableComment) -> {
            if (tableName != null && !tableName.isEmpty()) {
                TableDataResult result = ApiClient.getTableData(tableName);
                contentPanel.updateTableData(result.data, result.columnComments, result.keyColumn, tableName);
            } else {
                LogHandler.logError("Invalid table name received");
            }
        });
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }

    public static void startServer() {
        new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
                server.createContext("/api/login", new LoginHandler());
                server.createContext("/api/tables", new TablesHandler());
                server.createContext("/api/table/", new TableDataHandler());
                server.setExecutor(null);
                server.start();
                LogHandler.logInfo("Server started on port 8080");
            } catch (IOException e) {
                LogHandler.logError("Server error: " + e.getMessage(), e);
            }
        }).start();
    }

    public static void main(String[] args) {
        ContentPanel contentPanel = new ContentPanel();
        MenuPanel menuPanel = new MenuPanel();
        new MainCtrl(contentPanel, menuPanel);
        startServer();
    }
}