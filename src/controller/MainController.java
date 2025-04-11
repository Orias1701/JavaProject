package controller;

import model.ApiClient;
import model.ApiClient.TableDataResult;
import view.MainRegion.ContentPanel;
import view.MenuRegion.MenuPanel;

// import java.util.List;
// import java.util.Map;

public class MainController {
    private final ContentPanel contentPanel;
    private final MenuPanel menuPanel;

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }
    public MainController(ContentPanel contentPanel, MenuPanel menuPanel) {
        if (contentPanel == null || menuPanel == null) {
            throw new IllegalArgumentException("ContentPanel and MenuPanel must not be null");
        }
        this.contentPanel = contentPanel;
        this.menuPanel = menuPanel;

        menuPanel.setTableSelectionListener(tableName -> {
            if (tableName != null && !tableName.isEmpty()) {
                TableDataResult result = ApiClient.getTableData(tableName);
                contentPanel.updateTableData(result.data, result.columnComments);
            } else {
                System.err.println("Invalid table name received");
            }
        });
    }
}