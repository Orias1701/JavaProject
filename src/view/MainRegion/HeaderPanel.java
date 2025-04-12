package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import view.Style;
import java.util.function.Consumer;

public class HeaderPanel extends JPanel {
    private final JLabel tableNameLabel = new JLabel("");

    public HeaderPanel(Consumer<Void> addButtonCallback) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        setOpaque(false);

        tableNameLabel.setFont(Style.MONS_28);
        tableNameLabel.setForeground(Style.MAIN_CL);
        add(tableNameLabel, BorderLayout.WEST);

        Style.RoundedButton addRecordButton = new Style.RoundedButton(" + ");
        addRecordButton.setFont(Style.MONS_24);
        addRecordButton.setForeground(Style.LIGHT_CL);
        addRecordButton.setBackground(Style.MAIN_CL);
        addRecordButton.setFocusPainted(false);
        addRecordButton.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        addRecordButton.addActionListener(e -> addButtonCallback.accept(null));

        add(addRecordButton, BorderLayout.EAST);
    }

    public void updateTableNameLabel(String text) {
        tableNameLabel.setText(text != null ? text : "");
    }
}