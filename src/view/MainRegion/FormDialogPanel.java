package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import controller.LogHandler;
import controller.MainCtrl;
import model.ApiClient.ApiResponse;

public class FormDialogPanel implements FormDialogHandler {
    private final TablePanel tablePanel;

    public FormDialogPanel(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    @Override
    public void showFormDialog(String actionType, int rowIndex) {
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

        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setTitle(actionType.equals("add") ? "Thêm dữ liệu" :
                actionType.equals("edit") ? "Sửa dữ liệu" : "Xóa dữ liệu");
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(tablePanel.getColumnNames().size(), 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Map<String, JTextField> inputFields = new HashMap<>();

        for (int i = 0; i < tablePanel.getColumnNames().size(); i++) {
            String col = tablePanel.getColumnNames().get(i);
            JLabel label = new JLabel(col);
            JTextField field = new JTextField();

            if (!actionType.equals("add") && rowIndex >= 0) {
                Object cellValue = tablePanel.getTable().getValueAt(rowIndex, i);
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
                for (String col : tablePanel.getColumnNames()) {
                    if (inputFields.get(col).getText().isEmpty()) {
                        JOptionPane.showMessageDialog(tablePanel, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(tablePanel, "Thêm dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        tablePanel.refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(tablePanel, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    dialog.dispose();

                } else if (actionType.equals("edit")) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (String col : tablePanel.getColumnNames()) {
                        rowData.put(col, inputFields.get(col).getText());
                    }
                    ApiResponse response = MainCtrl.updateRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue, rowData);
                    if (response.success) {
                        JOptionPane.showMessageDialog(tablePanel, "Cập nhật dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        tablePanel.refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(tablePanel, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    dialog.dispose();

                } else if (actionType.equals("delete")) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Bạn có chắc chắn muốn xóa " + tablePanel.getKeyColumn() + ": " + keyValue + "?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ApiResponse response = MainCtrl.deleteRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue);
                        if (response.success) {
                            JOptionPane.showMessageDialog(tablePanel, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(tablePanel, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(tablePanel, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
}