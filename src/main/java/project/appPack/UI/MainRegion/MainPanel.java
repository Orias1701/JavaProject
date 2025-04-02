package project.appPack.UI.MainRegion;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class MainPanel extends JPanel {

    private final TablePanel tablePanel;
    private String currentTableName;

    public MainPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Trên cùng: Tên bảng
        JLabel tableNameLabel = new JLabel("No table selected");
        tableNameLabel.setFont(tableNameLabel.getFont().deriveFont(20f));
        tableNameLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 0));
        add(tableNameLabel, BorderLayout.NORTH);

        // 2. Phần công cụ
        ToolPanel toolPanel = new ToolPanel(this);
        add(toolPanel, BorderLayout.CENTER);

        // 3. Phần bảng
        tablePanel = new TablePanel();
        add(tablePanel, BorderLayout.SOUTH);

        // Cập nhật tên bảng
        this.addPropertyChangeListener("currentTableName", evt -> {
            tableNameLabel.setText(currentTableName);
            toolPanel.updateInputFields(currentTableName);
        });
    }

    public void updateTableData(String tableName) {
        this.currentTableName = tableName;
        firePropertyChange("currentTableName", null, tableName);
        tablePanel.updateTableData(tableName);
    }

    public JTable getTable() {
        return tablePanel.getTable();
    }
}