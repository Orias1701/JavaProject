package project.appPack.UI.MainRegion;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class InputPanel extends JPanel {
    private final JScrollPane inputScrollPane;

    public InputPanel() {
        setLayout(new java.awt.BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(null);

        inputScrollPane = new JScrollPane(inputPanel);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setPreferredSize(new Dimension(500, 300));
        inputScrollPane.setMinimumSize(new Dimension(500, 300));
        inputScrollPane.setBorder(null);
        customizeScrollBar(inputScrollPane);

        add(inputScrollPane, java.awt.BorderLayout.CENTER);
    }

    public void updateInputFields(JTable table, Map<String, JTextField> inputFields, String tableName) {
        JPanel inputPanel = (JPanel) inputScrollPane.getViewport().getView();
        inputPanel.removeAll();
        inputFields.clear();

        if (tableName == null) return;

        String[] columnNames = new String[table.getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = table.getColumnName(i);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        if (columnNames.length > 6) {
            for (int i = 0; i < columnNames.length; i++) {
                String column = columnNames[i];
                int colOffset = (i < columnNames.length / 2) ? 0 : 2;
                int row = (i < columnNames.length / 2) ? i : i - (columnNames.length / 2);

                gbc.gridx = colOffset;
                gbc.gridy = row;
                JLabel label = new JLabel(column + ":");
                label.setPreferredSize(new Dimension(100, 30));
                inputPanel.add(label, gbc);

                gbc.gridx = colOffset + 1;
                JTextField field = new JTextField(30);
                field.setPreferredSize(new Dimension(300, 30));
                inputPanel.add(field, gbc);
                inputFields.put(column, field);
            }
        } else {
            for (int i = 0; i < columnNames.length; i++) {
                String column = columnNames[i];
                gbc.gridx = 0;
                gbc.gridy = i;
                JLabel label = new JLabel(column + ":");
                label.setPreferredSize(new Dimension(100, 30));
                inputPanel.add(label, gbc);

                gbc.gridx = 1;
                JTextField field = new JTextField(30);
                field.setPreferredSize(new Dimension(300, 30));
                inputPanel.add(field, gbc);
                inputFields.put(column, field);
            }
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                String columnName = table.getColumnName(i);
                Object value = table.getValueAt(selectedRow, i);
                inputFields.get(columnName).setText(value != null ? value.toString() : "");
            }
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void customizeScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();

        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = java.awt.Color.decode("#D3D3D3");
                this.trackColor = java.awt.Color.WHITE;
            }

            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private javax.swing.JButton createZeroButton() {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        verticalBar.setPreferredSize(new Dimension(8, 0));

        horizontalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = java.awt.Color.decode("#D3D3D3");
                this.trackColor = java.awt.Color.WHITE;
            }

            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private javax.swing.JButton createZeroButton() {
                javax.swing.JButton button = new javax.swing.JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        horizontalBar.setPreferredSize(new Dimension(0, 8));
    }
}