// package view.MainRegion;

// import javax.swing.*;
// import javax.swing.table.DefaultTableCellRenderer;
// import java.awt.*;
// import view.Style;

// public class TableCellRenderer extends DefaultTableCellRenderer {

//     @Override
//     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//         Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//         if (c instanceof JLabel label) {
//             label.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
//                 BorderFactory.createEmptyBorder(0, 30, 0, 0)
//             ));
//             label.setFont(Style.ROB_14);
//             label.setHorizontalAlignment(SwingConstants.LEFT);
//             label.setOpaque(true);
//             if (isSelected) {
//                 label.setBackground(Style.SEC_CL);
//                 label.setForeground(Style.ACT_CL);
//             } else {
//                 label.setBackground(Style.LIGHT_CL);
//                 label.setForeground(Style.DARK_CL);
//             }
//         }
//         return c;
//     }
// }