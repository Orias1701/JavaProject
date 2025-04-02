package project.appPack.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class MenuPanel extends JPanel {

    public MenuPanel(final MainContentPanel mainContentPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        setBackground(Color.decode("#FFFFFF"));

        JLabel tableLabel = new JLabel("Table");
        tableLabel.setAlignmentX(CENTER_ALIGNMENT);
        tableLabel.setFont(tableLabel.getFont().deriveFont(20f));
        tableLabel.setForeground(Color.decode("#FF9500"));
        add(tableLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        List<String> tableNames = ApiClient.getTableNames();
        for (String name : tableNames) {
            MenuButton button = new MenuButton(name);
            button.setMaximumSize(new Dimension(130, 50));
            button.setPreferredSize(new Dimension(130, 50));
            button.setMinimumSize(new Dimension(130, 50));
            button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFont(button.getFont().deriveFont(15f));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#FF9500"), 1, true),
                new javax.swing.border.EmptyBorder(8, 12, 8, 12)
            ));

            button.addActionListener(e -> mainContentPanel.updateTableData(name));

            add(button);
            add(Box.createRigidArea(new Dimension(0, 7)));
        }
    }
}