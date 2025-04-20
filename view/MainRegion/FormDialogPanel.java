package view.MainRegion;

import controller.LogHandler;
import controller.MainCtrl;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import model.ApiClient.ApiResponse;
import view.Style;

/**
 * FormDialogPanel là lớp xử lý hiển thị form thêm/sửa/xóa dữ liệu
 * dựa trên bảng hiện tại được hiển thị trên TablePanel.
 */
public class FormDialogPanel implements FormDialogHandler {
    private final TablePanel tablePanel;

    public FormDialogPanel(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    @Override
    public void showFormDialog(String actionType, int rowIndex) {
        // Kiểm tra điều kiện đầu vào để tránh lỗi logic
        if (tablePanel.getKeyColumn() == null || tablePanel.getKeyColumn().isEmpty()) {
            JOptionPane.showMessageDialog(tablePanel, "Không tìm thấy khóa chính của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tablePanel.getTableName() == null || tablePanel.getTableName().isEmpty()) {
            JOptionPane.showMessageDialog(tablePanel, "Không xác định được tên bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!tablePanel.getColumnNames().contains(tablePanel.getKeyColumn())) {
            JOptionPane.showMessageDialog(tablePanel, " gegeKhóa chính không khớp với các cột của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!actionType.equals("add") && !actionType.equals("detail") && (rowIndex < 0 || rowIndex >= tablePanel.getTable().getRowCount())) {
            JOptionPane.showMessageDialog(tablePanel, "Hàng được chọn không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo dialog chứa form nhập liệu
        JDialog dialog = new JDialog((Frame) null, true);
        dialog.setTitle(actionType.equals("add") ? "Thêm dữ liệu" :
                actionType.equals("edit") ? "Sửa dữ liệu" :
                actionType.equals("delete") ? "Xóa dữ liệu" :
                actionType.equals("all") ? "Quản lý dữ liệu" : "Chi tiết dữ liệu");
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Style.LIGHT_CL);

        // Tạo panel chứa các trường nhập liệu theo dạng lưới 2 cột
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setBackground(Style.LIGHT_CL);
        Map<String, JTextField> inputFields = new HashMap<>();

        // Duyệt qua các cột của bảng để tạo label + textfield tương ứng
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

            // Nếu đang sửa, xóa, all hoặc chi tiết thì lấy giá trị hiện có từ bảng
            if (!actionType.equals("add") && rowIndex >= 0) {
                Object cellValue = tablePanel.getTable().getValueAt(rowIndex, i);
                field.setText(cellValue != null ? cellValue.toString() : "");
            }

            // Chỉ cho phép chỉnh sửa khi là add, edit hoặc all
            // field.setEditable(actionType.equals("add") || actionType.equals("edit") || actionType.equals("all"));

            if (actionType.equals("delete") || actionType.equals("detail") || (col.equals(tablePanel.getKeyColumn()) && !actionType.equals("add"))) {
                field.setEditable(false);
                field.setForeground(Style.ACT_CL);
                field.setBackground(Style.LIGHT_CL);
            }

            formPanel.add(label);
            formPanel.add(field);
            inputFields.put(col, field);
        }

        // Đặt formPanel vào JScrollPane (phòng khi nhiều dòng)
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Style.LIGHT_CL);

        // Tạo panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Style.LIGHT_CL);

        // Nút xác nhận (Thêm/Cập nhật/Xóa)
        Style.RoundedButton confirmButton = null;
        if (actionType.equals("add") || actionType.equals("edit") || actionType.equals("all")) {
            confirmButton = new Style.RoundedButton(
                    actionType.equals("add") ? "Thêm" : "Cập nhật"
            );
            confirmButton.setFont(Style.MONS_14);
            confirmButton.setBackground(Style.BLUE);
            confirmButton.setForeground(Color.WHITE);
            confirmButton.setPreferredSize(new Dimension(100, 40));

            confirmButton.addActionListener(e -> {
                // Kiểm tra dữ liệu đầu vào
                for (String col : tablePanel.getColumnNames()) {
                    String value = inputFields.get(col).getText();
                    if (value.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Kiểm tra email hợp lệ
                    if (col.toLowerCase().contains("email") && !value.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        JOptionPane.showMessageDialog(dialog, "Email không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
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
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(dialog, "Thêm dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        Map<String, Object> rowData = new HashMap<>();
                        for (String col : tablePanel.getColumnNames()) {
                            rowData.put(col, inputFields.get(col).getText());
                        }
                        ApiResponse response = MainCtrl.updateRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue, rowData);
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(dialog, "Cập nhật dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                } catch (Exception ex) {
                    LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(dialog, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            });
        } else if (actionType.equals("delete")) {
            confirmButton = new Style.RoundedButton("Xóa");
            confirmButton.setFont(Style.MONS_14);
            confirmButton.setBackground(Style.RED);
            confirmButton.setForeground(Color.WHITE);
            confirmButton.setPreferredSize(new Dimension(100, 40));

            confirmButton.addActionListener(e -> {
                String keyValue = inputFields.get(tablePanel.getKeyColumn()).getText();
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Bạn có chắc chắn muốn xóa " + tablePanel.getKeyColumn() + ": " + keyValue + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        ApiResponse response = MainCtrl.deleteRow(tablePanel.getTableName(), tablePanel.getKeyColumn(), keyValue);
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(dialog, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (Exception ex) {
                        LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(dialog, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            });
        }

        // Nút xóa cho trường hợp all
        Style.RoundedButton deleteButton = null;
        if (actionType.equals("all")) {
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
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(dialog, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            tablePanel.refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        LogHandler.logError("Lỗi kết nối: " + ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(dialog, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        // Nút hủy hoặc thoát
        Style.RoundedButton cancelButton = new Style.RoundedButton(actionType.equals("detail") ? "Thoát" : "Hủy");
        cancelButton.setFont(Style.MONS_14);
        cancelButton.setBackground(Style.GRAY_CL);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dialog.dispose());

        // Thêm nút vào panel theo loại hành động
        buttonPanel.add(cancelButton);
        if (deleteButton != null) {
            buttonPanel.add(deleteButton);
        }
        if (confirmButton != null) {
            buttonPanel.add(confirmButton);
        }

        // Thêm các phần tử vào dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Cấu hình kích thước và hiển thị dialog
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}