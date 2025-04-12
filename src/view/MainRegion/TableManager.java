// package view.MainRegion;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import javax.swing.table.JTableHeader;
// import javax.swing.table.TableColumn;
// import java.util.List;
// import java.util.Map;
// import java.awt.Color;
// import java.awt.Component;
// import java.awt.Dimension;
// import java.util.ArrayList;
// import util.Logger;

// public class TableManager {

//     private JTable table;
//     private DefaultTableModel tableModel;
//     private JScrollPane scrollPane;
//     private String keyColumn;
//     private String tableName;
//     private List<String> columnNames;
//     private FormDialog formDialog;

//     public TableManager() {
//         tableModel = new DefaultTableModel();
//         table = new JTable(tableModel);
//         table.setFillsViewportHeight(true);
//         table.setFont(view.Style.ROB_14);
//         table.setRowHeight(40);
//         table.setShowGrid(false);
//         table.setIntercellSpacing(new Dimension(0, 0));

//         table.setDefaultRenderer(Object.class, new TableCellRenderer());

//         JTableHeader header = table.getTableHeader();
//         header.setDefaultRenderer(new TableHeaderRenderer());
//         scrollPane = new JScrollPane(table);
//         scrollPane.setBorder(BorderFactory.createEmptyBorder());
//     }

//     public void setFormDialog(FormDialog formDialog) {
//         this.formDialog = formDialog;
//     }

//     public JScrollPane getScrollPane() {
//         return scrollPane;
//     }

//     public JTable getTable() {
//         return table;
//     }

//     public List<String> getColumnNames() {
//         return columnNames;
//     }

//     public String getKeyColumn() {
//         return keyColumn;
//     }

//     public String getTableName() {
//         return tableName;
//     }

//     public void refreshTable() {
//         try {
//             model.TableDataResult result = model.ApiService.getTableData(tableName);
//             if (result.data != null && !result.data.isEmpty()) {
//                 updateTableData(result.data, result.columnComments, keyColumn, tableName);
//             } else {
//                 tableModel.setRowCount(0);
//                 tableModel.setColumnCount(0);
//                 JOptionPane.showMessageDialog(table, "Không có dữ liệu để hiển thị sau khi làm mới", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
//             }
//         } catch (Exception ex) {
//             JOptionPane.showMessageDialog(table, "Lỗi khi làm mới dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//         }
//         table.revalidate();
//         table.repaint();
//     }

//     public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName) {
//         this.keyColumn = keyColumn;
//         this.tableName = tableName;
//         Logger.log("Khóa chính ContentPanel: " + keyColumn);
//         Logger.log("Tên bảng ContentPanel: " + tableName);
//         tableModel.setRowCount(0);
//         tableModel.setColumnCount(0);
        
//         if (data == null || data.isEmpty()) {
//             JOptionPane.showMessageDialog(table, "Không có dữ liệu để hiển thị", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
//             return;
//         }

//         Map<String, String> firstRow = data.get(0);
//         this.columnNames = new ArrayList<>(firstRow.keySet());
        
//         List<String> displayNames = new ArrayList<>();
//         for (String columnName : columnNames) {
//             String displayName = columnComments.getOrDefault(columnName, columnName);
//             displayNames.add(displayName);
//         }
        
//         displayNames.add("");
//         displayNames.add("");

//         tableModel.setColumnIdentifiers(displayNames.toArray());

//         for (Map<String, String> row : data) {
//             Object[] rowData = new Object[columnNames.size() + 2];
//             for (int i = 0; i < columnNames.size(); i++) {
//                 rowData[i] = row.get(columnNames.get(i));
//             }
//             rowData[columnNames.size()] = "Sửa";
//             rowData[columnNames.size() + 1] = "Xóa";
//             tableModel.addRow(rowData);
//         }

//         int editColumnIndex = table.getColumnCount() - 2;
//         int deleteColumnIndex = table.getColumnCount() - 1;

//         TableColumn editButton = table.getColumnModel().getColumn(editColumnIndex);
//         TableColumn deleteButton = table.getColumnModel().getColumn(deleteColumnIndex);

//         editButton.setCellRenderer(new ButtonRenderer());
//         editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", table, columnNames, formDialog));
//         editButton.setPreferredWidth(70);
//         editButton.setMaxWidth(70);
//         editButton.setMinWidth(70);
//         editButton.setResizable(false);

//         deleteButton.setCellRenderer(new ButtonRenderer());
//         deleteButton.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", table, columnNames, formDialog));
//         deleteButton.setPreferredWidth(90);
//         deleteButton.setMaxWidth(90);
//         deleteButton.setMinWidth(90);
//         deleteButton.setResizable(false);

//         deleteButton.setCellRenderer(new ButtonRenderer() {
//             @Override
//             public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                 if (c instanceof JButton button) {
//                     button.setForeground(Color.RED);
//                 }
//                 return c;
//             }
//         });

//         table.revalidate();
//         table.repaint();
//     }
// }