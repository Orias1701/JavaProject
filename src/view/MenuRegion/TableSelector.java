// package view.MenuRegion;

// import util.Logger;

// public class TableSelector {

//     private final ButtonManager buttonManager;
//     private String currentTableName;

//     // Interface cho listener
//     public interface TableSelectionListener {
//         void onTableSelected(String tableName);
//     }

//     private TableSelectionListener tableSelectionListener;

//     public TableSelector(ButtonManager buttonManager) {
//         this.buttonManager = buttonManager;
//         this.currentTableName = "HOME";
//         Logger.log("MenuPanel initialized");
//         setupButtonListeners();
//     }

//     private void setupButtonListeners() {
//         for (MenuButton button : buttonManager.getMenuButtons()) {
//             button.addActionListener(e -> {
//                 String tableName = (String) button.getClientProperty("tableName");
//                 if (!tableName.equals(currentTableName)) {
//                     currentTableName = tableName;
//                     Logger.log("Table name: " + currentTableName);
//                     if (tableSelectionListener != null && !"HOME".equals(currentTableName)) {
//                         tableSelectionListener.onTableSelected(currentTableName);
//                     }
//                 }
//             });
//         }
//     }

//     public void setTableSelectionListener(TableSelectionListener listener) {
//         this.tableSelectionListener = listener;
//     }

//     public String getCurrentTableName() {
//         return currentTableName;
//     }
// }