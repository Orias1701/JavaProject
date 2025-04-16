package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import controller.LogHandler;
import controller.MainCtrl;
import model.ApiClient.ApiResponse;
import view.Style;

public class FormDialogPanel implements FormDialogHandler {
    private final TablePanel tablePanel;

    public FormDialogPanel(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    @Override
    public void showFormDialog(String actionType, int rowIndex) {
        // Kiểm tra điều kiện đầu vào
        if (tablePanel.getKeyColumn() == null || tablePanel.getKeyColumn().isEmpty()) {
            JOptionPane.showMessageDialog(tablePanel, "Không tìm thấy khóa chính của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tablePanel.getTableName() == null || tablePanel.getTableName().isEmpty()) {
            JOptionPane.showMessageDialog(tablePanel, "Không xác định được tên bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!tablePanel.getColumnNames().contains(tablePanel.getKeyColumn())) {
            JOptionPane.showMessageDialog(tablePanel, "Khóa chính không khớp với các cột của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!actionType.equals("add") && (rowIndex < 0 || rowIndex >= tablePanel.getTable().getRowCount())) {
            JOptionPane.showMessageDialog(tablePanel, "Hàng được chọn không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo dialog
        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setTitle(actionType.equals("add") ? "Thêm dữ liệu" :
                actionType.equals("edit") ? "Sửa dữ liệu" : "Xóa dữ liệu");
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Style.LIGHT_CL);

        // Tạo form panel với JScrollPane
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setBackground(Style.LIGHT_CL);
        Map<String, JTextField> inputFields = new HashMap<>();

        // Tạo các trường nhập liệu
        for (int i = 0; i < tablePanel.getColumnNames().size(); i++) {
            String col = tablePanel.getColumnNames().get(i);
            String comment = tablePanel.getColumnComments().get(i);
            JLabel label = new JLabel(comment + ":");
            label.setFont(Style.MONS_14);
            label.setForeground(Style.DARK_CL);

            JTextField field = new JTextField(20);
            field.setFont(Style.MONS_14);
            field.setBorder(BorderFactory.createCompoundBorder(
                new Style.RoundBorder(Style.MAIN_CL, 10),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));

            if (!actionType.equals("add") && rowIndex >= 0) {
                Object cellValue = tablePanel.getTable().getValueAt(rowIndex, i);
                field.setText(cellValue != null ? cellValue.toString() : "");
            }

            field.setEditable(!actionType.equals("delete"));
            if (actionType.equals("edit") && col.equals(tablePanel.getKeyColumn())) {
                field.setEditable(false);
                field.setForeground(Style.ACT_CL);
                field.setBackground(Style.FTH_CL);
            }

            formPanel.add(label);
            formPanel.add(field);
            inputFields.put(col, field);
        }

        // Thêm JScrollPane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Style.LIGHT_CL);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Style.LIGHT_CL);

        // Sử dụng RoundedButton từ Style
        Style.RoundedButton confirmButton = new Style.RoundedButton(
                actionType.equals("add") ? "Thêm" :
                        actionType.equals("edit") ? "Cập nhật" : "Xóa"
        );
        confirmButton.setFont(Style.MONS_14);
        confirmButton.setBackground(Style.BLUE);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(100, 40));

        confirmButton.addActionListener(e -> {
            if (actionType.equals("add") || actionType.equals("edit")) {
                for (String col : tablePanel.getColumnNames()) {
                    String value = inputFields.get(col).getText();
                    if (value.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (col.toLowerCase().contains("email") && !value.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        JOptionPane.showMessageDialog(dialog, "Email không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String keyValue = inputFields.get(tablePanel.getKeyColumn()).getText();
            LogHandler.logInfo("showFormDialog: actionType=" + actionType + ", keyColumn=" + tablePanel.getKeyColumn() + ", keyValue=" + keyValue);

            try {
                if (actionType.equals("add")) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (String col : tablePanel.getColumnNames()) {
                        rowData.put(col, inputFields.get(col).getText());
                    }
                    ApiResponse response = MainCtrl.addRow(tablePanel.getTableName(), rowData);
                    if (response.success) {
                        JOptionPane.showMessageDialog(dialog, "Thêm dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        tablePanel.refreshTable();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else if (actionType.equals("edit")) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (String col : tablePanel.getColumnNames()) {
                        rowData.put(col, inputFields.get(col).getText());
                    }
                    ApiResponse response = MainCtrl.updateRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue, rowData);
                    if (response.success) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        tablePanel.refreshTable();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else if (actionType.equals("delete")) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Bạn có chắc chắn muốn xóa " + tablePanel.getKeyColumn() + ": " + keyValue + "?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ApiResponse response = MainCtrl.deleteRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue);
                        if (response.success) {
                            JOptionPane.showMessageDialog(dialog, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        // Thêm nút xóa cho dialog sửa
        Style.RoundedButton deleteButton = null;
        if (actionType.equals("edit")) {
            deleteButton = new Style.RoundedButton("Xóa");
            deleteButton.setFont(Style.MONS_14);
            deleteButton.setBackground(Style.RED);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setPreferredSize(new Dimension(100, 40));
            deleteButton.addActionListener(e -> {
                String keyValue = inputFields.get(tablePanel.getKeyColumn()).getText();
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Bạn có chắc chắn muốn xóa " + tablePanel.getKeyColumn() + ": " + keyValue + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        ApiResponse response = MainCtrl.deleteRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue);
                        if (response.success) {
                            JOptionPane.showMessageDialog(dialog, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(dialog, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        // Sử dụng RoundedButton cho nút Hủy
        Style.RoundedButton cancelButton = new Style.RoundedButton("Hủy");
        cancelButton.setFont(Style.MONS_14);
        cancelButton.setBackground(Style.DARK_CL);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dialog.dispose());

        // Thêm các nút vào buttonPanel
        buttonPanel.add(cancelButton);
        if (deleteButton != null) {
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(confirmButton);

        // Thêm các thành phần vào dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Cài đặt kích thước và vị trí
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}