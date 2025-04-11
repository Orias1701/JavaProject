package view.MainRegion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;
import java.util.Map;
import java.awt.*;
import java.util.ArrayList;
import view.Style;

public class ContentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        DefaultTableCellRenderer bottomBorderRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel label) {
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                        label.setForeground(table.getSelectionForeground());
                    } else {
                        label.setBackground(Color.WHITE);
                        label.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, bottomBorderRenderer);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Style.MAIN_CL);
                label.setForeground(Color.WHITE);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setHorizontalAlignment(CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
                return label;
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
        setOpaque(false);
    }

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for the selected table.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy danh sách tên cột thực từ bản ghi đầu tiên
        Map<String, String> firstRow = data.get(0);
        List<String> columnNames = new ArrayList<>(firstRow.keySet());
        
        // Tạo danh sách tên hiển thị từ comment
        List<String> displayNames = new ArrayList<>();
        for (String columnName : columnNames) {
            String displayName = columnComments.getOrDefault(columnName, columnName);
            displayNames.add(displayName);
        }
        
        // Thiết lập tên hiển thị cho JTable
        tableModel.setColumnIdentifiers(displayNames.toArray());

        // Thêm dữ liệu hàng
        for (Map<String, String> row : data) {
            Object[] rowData = new Object[columnNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }
            tableModel.addRow(rowData);
        }

        table.revalidate();
        table.repaint();
    }
}
