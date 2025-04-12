// package view.MainRegion;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionListener;
// import view.Style;

// public class HeaderPanel extends JPanel {

//     private JLabel tableNameLabel;
//     private JButton addRecordButton;

//     public HeaderPanel() {
//         setLayout(new BorderLayout());
//         setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//         setOpaque(false);

//         tableNameLabel = new JLabel("");
//         tableNameLabel.setFont(Style.MONS_16);
//         tableNameLabel.setForeground(Style.LIGHT_CL);
//         add(tableNameLabel, BorderLayout.WEST);

//         addRecordButton = new JButton("Thêm bản ghi");
//         addRecordButton.setFont(Style.MONS_16);
//         addRecordButton.setForeground(Style.LIGHT_CL);
//         addRecordButton.setBackground(Style.MAIN_CL);
//         addRecordButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//         add(addRecordButton, BorderLayout.EAST);
//     }

//     public void setAddButtonListener(ActionListener listener) {
//         addRecordButton.addActionListener(listener);
//     }
// }