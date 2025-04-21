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
    private final TablePanel parent;

    public TableView(TablePanel parent) {
        this.parent = parent;
        view = new JPanel(new BorderLayout());
        view.setOpaque(true);
        view.setBackground(Style.LIGHT_CL);

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
                            BorderFactory.createEmptyBorder(0, 20, 0, 15)
                    ));
                    label.setFont(Style.ROB_14);
                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(Style.SEC_CL);
                        label.setForeground(Style.ACT_CL);
                    } else {
                        label.setBackground(Style.LIGHT_CL);
                        label.setForeground(Style.DARK_CL);
                    }

                    // Log giá trị và kiểu
                    System.out.println("Value: " + value + ", Type: " + (value != null ? value.getClass().getSimpleName() : "null"));

                    // Lấy columnTypes, columnNames, columnComments từ TablePanel
                    List<String> columnTypes = parent.getColumnTypes();
                    List<String> columnNames = parent.getColumnNames();
                    List<String> columnComments = parent.getColumnComments();
                    String columnName = table.getColumnName(column);

                    // Log danh sách cột và kiểu
                    System.out.println("Column name (header): " + columnName);
                    System.out.println("Column names: " + columnNames);
                    System.out.println("Column comments: " + columnComments);
                    System.out.println("Column types: " + columnTypes);
                    System.out.println("Column types size: " + (columnTypes != null ? columnTypes.size() : "null"));

                    // Tìm columnIndex dựa trên columnComments trước, sau đó thử columnNames
                    int columnIndex = -1;
                    if (columnComments != null) {
                        columnIndex = columnComments.indexOf(columnName);
                    }
                    if (columnIndex < 0 && columnNames != null) {
                        columnIndex = columnNames.indexOf(columnName);
                    }

                    System.out.println("Column index: " + columnIndex);

                    // Căn chỉnh dựa trên kiểu dữ liệu
                    if (columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                        String dataType = columnTypes.get(columnIndex);
                        if (dataType.equalsIgnoreCase("decimal")) {
                            label.setHorizontalAlignment(SwingConstants.RIGHT);
                        } else {
                            label.setHorizontalAlignment(SwingConstants.LEFT);
                        }
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    // Xử lý ngày giờ nếu kiểu dữ liệu là datetime hoặc timestamp
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    if (value instanceof String str && columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                        String dataType = columnTypes.get(columnIndex);
                        System.out.println("Data type for column " + columnName + ": " + dataType);
                        if (dataType.equalsIgnoreCase("datetime") || dataType.equalsIgnoreCase("timestamp")) {
                            try {
                                // Thử định dạng đầy đủ: yyyy-MM-dd HH:mm:ss
                                java.text.SimpleDateFormat inputFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                java.util.Date date = inputFormatter.parse(str);
                                label.setText(formatter.format(date));
                                System.out.println("Parsed datetime (full): " + str + " → " + label.getText());
                            } catch (java.text.ParseException e1) {
                                try {
                                    // Thử định dạng có phút: yyyy-MM-dd HH:mm
                                    java.text.SimpleDateFormat fallbackFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    java.util.Date date = fallbackFormatter.parse(str);
                                    label.setText(formatter.format(date));
                                    System.out.println("Parsed datetime (with minutes): " + str + " → " + label.getText());
                                } catch (java.text.ParseException e2) {
                                    try {
                                        // Thử định dạng chỉ có giờ: yyyy-MM-dd HH
                                        java.text.SimpleDateFormat hourOnlyFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH");
                                        java.util.Date date = hourOnlyFormatter.parse(str);
                                        label.setText(formatter.format(date));
                                        System.out.println("Parsed datetime (hour only): " + str + " → " + label.getText());
                                    } catch (java.text.ParseException e3) {
                                        label.setText(str);
                                        System.out.println("Parse error for datetime value: " + str + " → " + e3.getMessage());
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("Skipped datetime formatting: " +
                            "value is " + (value instanceof String ? "String" : "not String") +
                            ", columnIndex=" + columnIndex +
                            ", columnTypes.size=" + (columnTypes != null ? columnTypes.size() : "null"));
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
                label.setPreferredSize(new Dimension(label.getWidth(), 40));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 1, 1, Style.LIGHT_CL),
                        BorderFactory.createEmptyBorder(0, 20, 0, 15)
                ));

                // Căn chỉnh tiêu đề dựa trên columnTypes
                List<String> columnTypes = parent.getColumnTypes();
                List<String> columnComments = parent.getColumnComments();
                String columnName = table.getColumnName(column);
                int columnIndex = columnComments != null ? columnComments.indexOf(columnName) : -1;
                if (columnIndex < 0 && parent.getColumnNames() != null) {
                    columnIndex = parent.getColumnNames().indexOf(columnName);
                }

                if (columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                    String dataType = columnTypes.get(columnIndex);
                    if (dataType.equalsIgnoreCase("decimal")) {
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }

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

    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, FormDialogHandler formDialogHandler, boolean canAdd, boolean canEdit, boolean canDelete) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            table.revalidate();
            table.repaint();
            return;
        }

        List<String> displayNames = new ArrayList<>(columnComments);
        displayNames.add("");
        if (canDelete) {
            displayNames.add("");
        }

        tableModel.setColumnIdentifiers(displayNames.toArray());
        for (Map<String, String> row : data) {
            Object[] rowData = new Object[displayNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }
            if (canEdit) {
                rowData[columnNames.size()] = "Sửa";
            } else {
                rowData[columnNames.size()] = "Chi tiết";
            }

            if (canDelete) {
                rowData[columnNames.size() + (canEdit ? 1 : 0)] = "Xóa";
            }

            tableModel.addRow(rowData);
        }

        if (canEdit) {
            int editColumnIndex = table.getColumnCount() - (canDelete ? 2 : 1);
            TableColumn editButton = table.getColumnModel().getColumn(editColumnIndex);
            editButton.setCellRenderer(new ButtonRenderer());
            editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", formDialogHandler));
            editButton.setPreferredWidth(70);
            editButton.setMaxWidth(70);
            editButton.setMinWidth(70);
            editButton.setResizable(false);
        } else {
            int detailColumnIndex = table.getColumnCount() - 1;
            TableColumn detailButton = table.getColumnModel().getColumn(detailColumnIndex);
            detailButton.setCellRenderer(new ButtonRenderer());
            detailButton.setCellEditor(new ButtonEditor(new JCheckBox(), "detail", formDialogHandler));
            detailButton.setPreferredWidth(120);
            detailButton.setMaxWidth(120);
            detailButton.setMinWidth(120);
            detailButton.setResizable(false);
        }
    
        if (canDelete) {
            int deleteColumnIndex = table.getColumnCount() - 1;
            TableColumn deleteButton = table.getColumnModel().getColumn(deleteColumnIndex);
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
        }
    
        table.revalidate();
        table.repaint();
    }
}