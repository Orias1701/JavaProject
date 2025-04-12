// package view.MainRegion;

// import javax.swing.*;
// import java.util.List;
// import java.util.Map;
// import java.awt.*;
// import java.util.HashMap;
// import util.Logger;

// public class FormDialog {

//     private ContentPanel parent;
//     private TableManager tableManager;

//     public FormDialog(ContentPanel parent, TableManager tableManager) {
//         this.parent = parent;
//         this.tableManager = tableManager;
//     }

//     public void show(String actionType, int rowIndex, List<String> columnNames) {
//         String keyColumn = tableManager.getKeyColumn();
//         String tableName = tableManager.getTableName();
//         JTable table = tableManager.getTable();

//         if (keyColumn == null || keyColumn.isEmpty()) {
//             JOptionPane.showMessageDialog(parent, "Không tìm thấy khóa chính của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
//             return;
//         }
//         if (tableName == null || tableName.isEmpty()) {
//             JOptionPane.showMessageDialog(parent, "Không xác định được tên bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
//             return;
//         }
//         if (!columnNames.contains(keyColumn)) {
//             JOptionPane.showMessageDialog(parent, "Khóa chính không khớp với các cột của bảng", "Lỗi", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         JDialog dialog = new JDialog((Frame) null, true);
//         dialog.setTitle(actionType.equals("add") ? "Thêm dữ liệu" :
//                         actionType.equals("edit") ? "Sửa dữ liệu" : "Xóa dữ liệu");
//         dialog.setLayout(new BorderLayout());

//         JPanel formPanel = new JPanel(new GridLayout(columnNames.size(), 2, 5, 5));
//         formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//         Map<String, JTextField> inputFields = new HashMap<>();

//         for (int i = 0; i < columnNames.size(); i++) {
//             String col = columnNames.get(i);
//             JLabel label = new JLabel(col);
//             JTextField field = new JTextField();

//             if (!actionType.equals("add") && rowIndex >= 0) {
//                 Object cellValue = table.getValueAt(rowIndex, i);
//                 field.setText(cellValue != null ? cellValue.toString() : "");
//             }

//             field.setEditable(!actionType.equals("delete"));

//             formPanel.add(label);
//             formPanel.add(field);
//             inputFields.put(col, field);
//         }

//         JButton confirmButton = new JButton(
//                 actionType.equals("add") ? "Thêm" :
//                 actionType.equals("edit") ? "Cập nhật" : "Xóa"
//         );

//         confirmButton.addActionListener(e -> {
//             if (actionType.equals("add") || actionType.equals("edit")) {
//                 for (String col : columnNames) {
//                     if (inputFields.get(col).getText().isEmpty()) {
//                         JOptionPane.showMessageDialog(parent, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                         return;
//                     }
//                 }
//             }

//             String keyValue = inputFields.get(keyColumn).getText();
//             Logger.log("showFormDialog: actionType=" + actionType + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);

//             try {
//                 if (actionType.equals("add")) {
//                     Map<String, Object> rowData = new HashMap<>();
//                     for (String col : columnNames) {
//                         rowData.put(col, inputFields.get(col).getText());
//                     }
//                     model.ApiService.addRow(tableName, rowData);
//                     JOptionPane.showMessageDialog(parent, "Thêm dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
//                     tableManager.refreshTable();
//                     dialog.dispose();

//                 } else if (actionType.equals("edit")) {
//                     Map<String, Object> rowData = new HashMap<>();
//                     for (String col : columnNames) {
//                         rowData.put(col, inputFields.get(col).getText());
//                     }
//                     model.ApiService.updateRow(tableName, keyColumn, keyValue, rowData);
//                     JOptionPane.showMessageDialog(parent, "Cập nhật dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
//                     tableManager.refreshTable();
//                     dialog.dispose();

//                 } else if (actionType.equals("delete")) {
//                     int confirm = JOptionPane.showConfirmDialog(dialog,
//                             "Bạn có chắc chắn muốn xóa " + keyColumn + ": " + keyValue + "?",
//                             "Xác nhận", JOptionPane.YES_NO_OPTION);
//                     if (confirm == JOptionPane.YES_OPTION) {
//                         model.ApiService.deleteRow(tableName, keyColumn, keyValue);
//                         JOptionPane.showMessageDialog(parent, "Xóa dữ liệu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
//                         tableManager.refreshTable();
//                         dialog.dispose();
//                     }
//                 }
//             } catch (Exception ex) {
//                 JOptionPane.showMessageDialog(parent, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//                 dialog.dispose();
//             }
//         });

//         JButton cancelButton = new JButton("Hủy");
//         cancelButton.addActionListener(e -> dialog.dispose());

//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//         buttonPanel.add(cancelButton);
//         buttonPanel.add(confirmButton);

//         dialog.add(formPanel, BorderLayout.CENTER);
//         dialog.add(buttonPanel, BorderLayout.SOUTH);
//         dialog.pack();
//         dialog.setLocationRelativeTo(null);
//         dialog.setVisible(true);
//     }
// }