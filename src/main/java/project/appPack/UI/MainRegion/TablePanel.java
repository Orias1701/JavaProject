package project.appPack.UI.MainRegion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import project.appPack.UI.ApiClient;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public TablePanel() {
        setLayout(new java.awt.BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tăng kích thước cell
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setShowGrid(false);

        // Tùy chỉnh header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#FF9500"));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Tùy chỉnh viền và renderer cho các cell
        Border lightOrangeBorder = BorderFactory.createLineBorder(Color.decode("#FFB580"), 1);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 14));
                setBorder(lightOrangeBorder);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Tùy chỉnh viền cho header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.decode("#FF9500"));
                c.setFont(new Font("Arial", Font.BOLD, 14));
                setBorder(lightOrangeBorder);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                return c;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, java.awt.BorderLayout.CENTER);
    }

    public void updateTableData(String tableName) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        List<Map<String, Object>> tableData = ApiClient.getTableData(tableName);
        if (tableData == null || tableData.isEmpty()) {
            tableModel.addColumn("Message");
            tableModel.addRow(new Object[]{"No data available"});
            return;
        }

        Map<String, Object> firstRow = tableData.get(0);
        String[] columnNames = firstRow.keySet().toArray(String[]::new);
        tableModel.setColumnIdentifiers(columnNames);

        for (Map<String, Object> row : tableData) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                rowData[i] = row.get(columnNames[i]);
            }
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public JTable getTable() {
        return table;
    }
}