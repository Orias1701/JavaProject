package controller;

import model.ApiClient;
import model.TableDataOperationsClient;
import view.MainRegion.ContentPanel;
import view.MenuRegion.MenuPanel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
// import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

public class MainCtrl {
    // private static final Logger LOGGER = Logger.getLogger(MainCtrl.class.getName());
    private final ContentPanel contentPanel;
    private final MenuPanel menuPanel;
    private static TableDataOperationsClient operationsClient;

    public MainCtrl(ContentPanel contentPanel, MenuPanel menuPanel) {
        if (contentPanel == null || menuPanel == null) {
            throw new IllegalArgumentException("ContentPanel and MenuPanel must not be null");
        }
        this.contentPanel = contentPanel;
        this.menuPanel = menuPanel;

        menuPanel.setTableSelectionListener((tableName, tableComment) -> {
            if (tableName != null && !tableName.isEmpty()) {
                ApiClient.TableDataResult result = ApiClient.getTableData(tableName);
                contentPanel.updateTableData(result.data, result.columnComments, result.keyColumn, tableName, tableComment);
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

    public static void setAuthHeader(String authHeader) {
        operationsClient = new TableDataOperationsClient(authHeader);
        LogHandler.logInfo("TableDataOperationsClient được khởi tạo với authHeader");
    }

    public static ApiClient.ApiResponse addRow(String tableName, Map<String, Object> data) {
        if (operationsClient == null) {
            LogHandler.logError("Không thể thực hiện addRow: operationsClient là null. Người dùng có thể chưa đăng nhập.");
            return new ApiClient.ApiResponse(false, "Vui lòng đăng nhập trước khi thực hiện thao tác này");
        }
        return operationsClient.addRow(tableName, data);
    }

    public static ApiClient.ApiResponse updateRow(String tableName, String keyColumn, Object keyValue, Map<String, Object> data) {
        if (operationsClient == null) {
            LogHandler.logError("Không thể thực hiện updateRow: operationsClient là null. Người dùng có thể chưa đăng nhập.");
            return new ApiClient.ApiResponse(false, "Vui lòng đăng nhập trước khi thực hiện thao tác này");
        }
        return operationsClient.updateRow(tableName, keyColumn, keyValue, data);
    }

    public static ApiClient.ApiResponse deleteRow(String tableName, String keyColumn, Object keyValue) {
        if (operationsClient == null) {
            LogHandler.logError("Không thể thực hiện deleteRow: operationsClient là null. Người dùng có thể chưa đăng nhập.");
            return new ApiClient.ApiResponse(false, "Vui lòng đăng nhập trước khi thực hiện thao tác này");
        }
        return operationsClient.deleteRow(tableName, keyColumn, keyValue);
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
                LogHandler.logError("Lỗi server: " + e.getMessage(), e);
            }
        }).start();
    }
}