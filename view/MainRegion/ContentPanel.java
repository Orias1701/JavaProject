package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;
import view.HomePanel;

public class ContentPanel extends JPanel {
    private HeaderPanel headerPanel;
    private TablePanel tablePanel;
    private HomePanel homePanel;
    private boolean isHomeDisplayed;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        headerPanel = new HeaderPanel(this::onAddButtonClicked);
        tablePanel = new TablePanel(this);
        homePanel = new HomePanel();
        isHomeDisplayed = false;

        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void onAddButtonClicked(Void ignored) {
        tablePanel.showAddFormDialog();
    }

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName, String tableComment) {

        if (isHomeDisplayed) {
            remove(homePanel);
            headerPanel = new HeaderPanel(this::onAddButtonClicked);
            tablePanel = new TablePanel(this);
            add(headerPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            isHomeDisplayed = false;
        }

        tablePanel.updateTableData(data, columnComments, keyColumn, tableName, tableComment);
        headerPanel.updateTableNameLabel(tableComment != null && !tableComment.isEmpty() ? tableComment : tableName);

        revalidate();
        repaint();
    }

    public void showHomePanel() {

        if (!isHomeDisplayed) {
            remove(headerPanel);
            remove(tablePanel);
            add(homePanel, BorderLayout.CENTER);
            isHomeDisplayed = true;
            revalidate();
            repaint();
        }
    }
}