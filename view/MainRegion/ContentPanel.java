package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

public class ContentPanel extends JPanel {
    private final TablePanel tablePanel;
    private final HeaderPanel headerPanel;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        headerPanel = new HeaderPanel(this::onAddButtonClicked);
        tablePanel = new TablePanel(this);

        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void onAddButtonClicked(Void ignored) {
        tablePanel.showAddFormDialog();
    }

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName, String tableComment) {
        tablePanel.updateTableData(data, columnComments, keyColumn, tableName, tableComment);
        headerPanel.updateTableNameLabel(tableComment != null && !tableComment.isEmpty() ? tableComment : tableName);
    }
}