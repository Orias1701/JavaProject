// package view.MainRegion;

// import javax.swing.*;
// import java.awt.*;

// public class ContentPanel extends JPanel {

//     private HeaderPanel headerPanel;
//     private TableManager tableManager;
//     private FormDialog formDialog;

//     public ContentPanel() {
//         setLayout(new BorderLayout());
//         setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//         tableManager = new TableManager();
//         formDialog = new FormDialog(this, tableManager);
//         // Modified: Pass formDialog to TableManager
//         tableManager.setFormDialog(formDialog);

//         headerPanel = new HeaderPanel();
//         headerPanel.setAddButtonListener(e -> {
//             if (tableManager.getColumnNames() == null || tableManager.getColumnNames().isEmpty()) {
//                 JOptionPane.showMessageDialog(this, "Bảng chưa được tải dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                 return;
//             }
//             formDialog.show("add", -1, tableManager.getColumnNames());
//         });
//         add(headerPanel, BorderLayout.NORTH);

//         add(tableManager.getScrollPane(), BorderLayout.CENTER);

//         setOpaque(false);
//     }

//     public void updateTableData(java.util.List<java.util.Map<String, String>> data, java.util.Map<String, String> columnComments, String keyColumn, String tableName) {
//         tableManager.updateTableData(data, columnComments, keyColumn, tableName);
//     }
// }