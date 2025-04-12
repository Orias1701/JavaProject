package view.MainRegion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import controller.LogHandler;
import controller.MainCtrl;
import model.ApiClient;
import model.ApiClient.ApiResponse;
import model.ApiClient.TableDataResult;

import java.util.List;
import java.util.Map;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import view.Style;

public class ContentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel tableNameLabel;
    private String keyColumn;
    private String tableName;
    
    private List<String> columnNames;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.setOpaque(false);

        tableNameLabel = new JLabel("");
        tableNameLabel.setFont(Style.MONS_16);
        tableNameLabel.setForeground(Style.LIGHT_CL);
        headerPanel.add(tableNameLabel, BorderLayout.WEST);

        JButton addRecordButton = new JButton("Thêm bản ghi");
        addRecordButton.setFont(Style.MONS_16);
        addRecordButton.setForeground(Style.LIGHT_CL);
        addRecordButton.setBackground(Style.MAIN_CL);
        addRecordButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        addRecordButton.addActionListener(e -> {
            if (columnNames == null || columnNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bảng chưa được tải dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showFormDialog("add", -1, columnNames);
        });
        headerPanel.add(addRecordButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setFont(Style.ROB_14);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        DefaultTableCellRenderer bottomBorderRenderer = new DefaultTableCellRenderer() {
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
                }
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, bottomBorderRenderer);

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
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                    BorderFactory.createEmptyBorder(0, 30, 0, 0)
                ));
                return label;
            }
        });
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        setOpaque(false);
    }

    private void showFormDialog(String actionType, int rowIndex, List<String> columnNames) {
        if (keyColumn == null || keyColumn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khóa chính của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tableName == null || tableName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không xác định được tên bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        if (!columnNames.contains(keyColumn)) {
            JOptionPane.showMessageDialog(this, "Khóa chính không khớp với các cột của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setTitle(actionType.equals("add") ? "Thêm dữ liệu" :
                        actionType.equals("edit") ? "Sửa dữ liệu" : "Xóa dữ liệu");
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(columnNames.size(), 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Map<String, JTextField> inputFields = new HashMap<>();

        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            JLabel label = new JLabel(col);
            JTextField field = new JTextField();

            if (!actionType.equals("add") && rowIndex >= 0) {
                Object cellValue = table.getValueAt(rowIndex, i);
                field.setText(cellValue != null ? cellValue.toString() : "");
            }

            field.setEditable(!actionType.equals("delete"));

            formPanel.add(label);
            formPanel.add(field);
            inputFields.put(col, field);
        }

        JButton confirmButton = new JButton(
                actionType.equals("add") ? "Thêm" :
                actionType.equals("edit") ? "Cập nhật" : "Xóa"
        );

        confirmButton.addActionListener(e -> {
            if (actionType.equals("add") || actionType.equals("edit")) {
                for (String col : columnNames) {
                    if (inputFields.get(col).getText().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String keyValue = inputFields.get(keyColumn).getText();
            LogHandler.logInfo("showFormDialog: actionType=" + actionType + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);

            try {
                if (actionType.equals("add")) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (String col : columnNames) {
                        rowData.put(col, inputFields.get(col).getText());
                    }
                    ApiResponse response = MainCtrl.addRow(tableName, rowData);
                    if (response.success) {
                        JOptionPane.showMessageDialog(this, "Thêm dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    dialog.dispose();

                } else if (actionType.equals("edit")) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (String col : columnNames) {
                        rowData.put(col, inputFields.get(col).getText());
                    }
                    ApiResponse response = MainCtrl.updateRow(tableName, keyColumn, keyValue, rowData);
                    if (response.success) {
                        JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    dialog.dispose();

                } else if (actionType.equals("delete")) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Bạn có chắc chắn muốn xóa " + keyColumn + ": " + keyValue + "?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ApiResponse response = MainCtrl.deleteRow(tableName, keyColumn, keyValue);
                        if (response.success) {
                            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void refreshTable() {
        try {
            TableDataResult result = ApiClient.getTableData(tableName);
            if (result.data != null && !result.data.isEmpty()) {
                updateTableData(result.data, result.columnComments, result.keyColumn, tableName);
            } else {
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để hiển thị sau khi làm mới", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            LogHandler.logError("Lỗi khi làm mới dữ liệu: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Lỗi khi làm mới dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        table.revalidate();
        table.repaint();
    }

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName) {
        this.keyColumn = keyColumn;
        this.tableName = tableName;
        LogHandler.logInfo("Khóa chính ContentPanel: " + keyColumn);
        LogHandler.logInfo("Tên bảng ContentPanel: " + tableName);
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để hiển thị", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> firstRow = data.get(0);
        
        this.columnNames = new ArrayList<>(firstRow.keySet());
        
        List<String> displayNames = new ArrayList<>();
        for (String columnName : columnNames) {
            String displayName = columnComments.getOrDefault(columnName, columnName);
            displayNames.add(displayName);
        }
        
        displayNames.add("");
        displayNames.add("");

        tableModel.setColumnIdentifiers(displayNames.toArray());

        for (Map<String, String> row : data) {
            Object[] rowData = new Object[columnNames.size() + 2];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }
            rowData[columnNames.size()] = "Sửa";
            rowData[columnNames.size() + 1] = "Xóa";
            tableModel.addRow(rowData);
        }

        class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
            public ButtonRenderer() {
                setOpaque(true);
                setFont(Style.ROB_B14);
                setBackground(Style.NO_CL);
                setForeground(Style.ACT_CL);
                setPreferredSize(new Dimension(70, getHeight()));
                setMaximumSize(new Dimension(70, getHeight()));
                setMinimumSize(new Dimension(70, getHeight()));
                setMargin(new Insets(0, 0, 0, 0));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL));
            }
        
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }

        class ButtonEditor extends DefaultCellEditor {
            private JButton button;
            private int row;
            @SuppressWarnings("unused")
            private String actionType;
            @SuppressWarnings("unused")
            private JTable table;
            @SuppressWarnings("unused")
            private List<String> columnNames;
            
            public ButtonEditor(JCheckBox checkBox, String actionType, JTable table, List<String> columnNames) {
                super(checkBox);
                this.actionType = actionType;
                this.table = table;
                this.columnNames = columnNames;

                button = new JButton();
                button.addActionListener(e -> {
                    fireEditingStopped();
                    ContentPanel.this.showFormDialog(actionType, row, columnNames);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                button.setText((value == null) ? "" : value.toString());
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return button.getText();
            }
        }

        int editColumnIndex = table.getColumnCount() - 2;
        int deleteColumnIndex = table.getColumnCount() - 1;

        TableColumn editButton = table.getColumnModel().getColumn(editColumnIndex);
        TableColumn deleteButton = table.getColumnModel().getColumn(deleteColumnIndex);

        editButton.setCellRenderer(new ButtonRenderer());
        editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", table, columnNames));
        editButton.setPreferredWidth(70);
        editButton.setMaxWidth(70);
        editButton.setMinWidth(70);
        editButton.setResizable(false);

        deleteButton.setCellRenderer(new ButtonRenderer());
        deleteButton.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", table, columnNames));
        deleteButton.setPreferredWidth(90);
        deleteButton.setMaxWidth(90);
        deleteButton.setMinWidth(90);
        deleteButton.setResizable(false);

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

        table.revalidate();
        table.repaint();
    }
}