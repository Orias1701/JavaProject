// package view.MainRegion;

// import javax.swing.*;
// import javax.swing.table.DefaultTableCellRenderer;
// import java.awt.*;
// import view.Style;

// public class TableHeaderRenderer extends DefaultTableCellRenderer {

//     @Override
//     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//         JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//         label.setFont(Style.ROB_16);
//         label.setBackground(Style.MAIN_CL);
//         label.setForeground(Style.LIGHT_CL);
//         label.setHorizontalAlignment(SwingConstants.LEFT);
//         label.setPreferredSize(new Dimension(label.getWidth(), 40));
//         label.setBorder(BorderFactory.createCompoundBorder(
//             BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
//             BorderFactory.createEmptyBorder(0, 30, 0, 0)
//         ));
//         return label;
//     }
// }