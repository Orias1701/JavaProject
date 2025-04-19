package view.MainRegion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;
import view.Style;

public class TableView {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JPanel view;

    public TableView() {
        view = new JPanel(new BorderLayout());
        view.setOpaque(true);
        view.setBackground(Style.LIGHT_CL);

        // Initialize JTable
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setFont(Style.ROB_14);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel label) {
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                            BorderFactory.createEmptyBorder(0, 30, 0, 0)
                    ));
                    label.setFont(Style.ROB_14);
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(Style.SEC_CL);
                        label.setForeground(Style.ACT_CL);
                    } else {
                        label.setBackground(Style.LIGHT_CL);
                        label.setForeground(Style.DARK_CL);
                    }
        
                    // Xử lý ngày giờ
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    if (value instanceof java.util.Date date) {
                        System.out.println("Date: " + date);
                        label.setText(formatter.format(date));
                    } else if (value instanceof String str && (table.getColumnName(column).equals("Ngày nhận phòng") || table.getColumnName(column).equals("Ngày trả phòng"))) {
                        try {
                            // Giả định chuỗi có định dạng yyyy-MM-dd HH:mm:ss hoặc yyyy-MM-dd HH
                            java.text.SimpleDateFormat inputFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            java.util.Date date = inputFormatter.parse(str);
                            label.setText(formatter.format(date));
                        } catch (java.text.ParseException e) {
                            try {
                                // Thử định dạng khác nếu cần, ví dụ yyyy-MM-dd HH
                                java.text.SimpleDateFormat fallbackFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH");
                                java.util.Date date = fallbackFormatter.parse(str);
                                label.setText(formatter.format(date));
                            } catch (java.text.ParseException ex) {
                                // Nếu không parse được, hiển thị chuỗi gốc
                                label.setText(str);
                            }
                        }
                    }
                }
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(Style.ROB_16);
                label.setBackground(Style.MAIN_CL);
                label.setForeground(Style.LIGHT_CL);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setPreferredSize(new Dimension(label.getWidth(), 40));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 1, 1, Style.LIGHT_CL),
                        BorderFactory.createEmptyBorder(0, 30, 0, 0)
                ));
                return label;
            }
        });

        view.add(table.getTableHeader(), BorderLayout.NORTH);
        view.add(table, BorderLayout.CENTER);
    }

    public JPanel getView() {
        return view;
    }

    public JTable getTable() {
        return table;
    }

    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, FormDialogHandler formDialogHandler) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            table.revalidate();
            table.repaint();
            return;
        }

        // Set column identifiers
        List<String> displayNames = new ArrayList<>(columnComments);
        displayNames.add("");
        displayNames.add("");
        tableModel.setColumnIdentifiers(displayNames.toArray());

        // Populate table data
        for (Map<String, String> row : data) {
            Object[] rowData = new Object[columnNames.size() + 2];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }
            rowData[columnNames.size()] = "Sửa";
            rowData[columnNames.size() + 1] = "Xóa";
            tableModel.addRow(rowData);
        }

        // Configure edit and delete buttons
        int editColumnIndex = table.getColumnCount() - 2;
        int deleteColumnIndex = table.getColumnCount() - 1;

        TableColumn editButton = table.getColumnModel().getColumn(editColumnIndex);
        TableColumn deleteButton = table.getColumnModel().getColumn(deleteColumnIndex);

        editButton.setCellRenderer(new ButtonRenderer());
        editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", formDialogHandler));
        editButton.setPreferredWidth(70);
        editButton.setMaxWidth(70);
        editButton.setMinWidth(70);
        editButton.setResizable(false);

        deleteButton.setCellRenderer(new ButtonRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JButton button) {
                    button.setForeground(Color.RED);
                }
                return c;
            }
        });
        deleteButton.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", formDialogHandler));
        deleteButton.setPreferredWidth(70);
        deleteButton.setMaxWidth(70);
        deleteButton.setMinWidth(70);
        deleteButton.setResizable(false);

        table.revalidate();
        table.repaint();
    }
}