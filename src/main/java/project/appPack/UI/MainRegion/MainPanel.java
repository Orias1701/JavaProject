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

        // HeaderPanel chứa tableNameLabel và ToolPanel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // 1. Trên cùng: Tên bảng
        JLabel tableNameLabel = new JLabel("No table selected");
        tableNameLabel.setFont(tableNameLabel.getFont().deriveFont(20f));
        tableNameLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 0));
        headerPanel.add(tableNameLabel, BorderLayout.NORTH);

        // 2. Phần công cụ (chiều cao cố định)
        ToolPanel toolPanel = new ToolPanel(this);
        headerPanel.add(toolPanel, BorderLayout.CENTER);

        // Thêm headerPanel vào NORTH
        add(headerPanel, BorderLayout.NORTH);

        // 3. Phần bảng (chiều cao linh hoạt)
        tablePanel = new TablePanel();
        add(tablePanel, BorderLayout.CENTER); // Đặt ở CENTER để linh hoạt chiều cao

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