package project.appPack.UI.MainRegion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import project.appPack.UI.ApiClient;
import project.appPack.UI.MenuRegion.MenuButton;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public TablePanel() {
        setLayout(new java.awt.BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setShowGrid(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#FF9500"));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new java.awt.Dimension(header.getWidth(), 40));

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
        tableScrollPane.setMinimumSize(null);
        add(tableScrollPane, java.awt.BorderLayout.CENTER);
    }

    public void updateTableData(String tableName) {
        List<Map<String, String>> data = ApiClient.getTableData(tableName);
        if (!data.isEmpty()) {
            String[] columnNames = data.get(0).keySet().toArray(String[]::new);
            String[][] rowData = new String[data.size()][columnNames.length];
            for (int i = 0; i < data.size(); i++) {
                Map<String, String> row = data.get(i);
                for (int j = 0; j < columnNames.length; j++) {
                    rowData[i][j] = row.get(columnNames[j]);
                }
            }
            tableModel.setDataVector(rowData, columnNames);
        } else {
            tableModel.setDataVector(new String[][]{}, new String[]{});
        }
    }

    public void createMenuPanel(MainPanel mainPanel) {
        Map<String, String> tableInfo = ApiClient.getTableInfo();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (!tableInfo.containsKey("error")) {
            for (Map.Entry<String, String> entry : tableInfo.entrySet()) {
                String tableName = entry.getKey();
                String tableComment = entry.getValue();
                MenuButton button = new MenuButton(tableComment);
                button.addActionListener(e -> mainPanel.updateTableData(tableName)); // Gọi với tableName
                add(button);
            }
        } else {
            JOptionPane.showMessageDialog(this, tableInfo.get("error"), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public JTable getTable() {
        return table;
    }
}