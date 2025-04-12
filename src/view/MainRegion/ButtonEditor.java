// package view.MainRegion;

// import javax.swing.*;
// import java.util.List;
// import java.awt.Component;

// public class ButtonEditor extends DefaultCellEditor {

//     private int row;
//     private JTable table;
//     private JButton button;
//     private String actionType;
//     private FormDialog formDialog;
//     private List<String> columnNames;

//     public JTable getTable() {
//         return table;
//     }
//     public String getActionType() {
//         return actionType;
//     }
//     public FormDialog getFormDialog() {
//         return formDialog;
//     }    
//     public List<String> getColumnNames() {
//         return columnNames;
//     }

//     public ButtonEditor(JCheckBox checkBox, String actionType, JTable table, List<String> columnNames, FormDialog formDialog) {
//         super(checkBox);
//         this.actionType = actionType;
//         this.table = table;
//         this.columnNames = columnNames;
//         this.formDialog = formDialog;

//         button = new JButton();
//         button.addActionListener(e -> {
//             fireEditingStopped();
//             formDialog.show(actionType, row, columnNames);
//         });
//     }

//     @Override
//     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//         this.row = row;
//         button.setText((value == null) ? "" : value.toString());
//         return button;
//     }

//     @Override
//     public Object getCellEditorValue() {
//         return button.getText();
//     }
// }