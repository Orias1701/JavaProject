package project.appPack.UI.MainRegion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import project.appPack.UI.ApiClient;

public class ToolPanel extends JPanel {
    private final MainPanel parent;
    private final InputPanel inputPanel;
    private final Map<String, javax.swing.JTextField> inputFields = new HashMap<>();
    private String currentTableName;

    public ToolPanel(MainPanel parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1120, 400));
        setMinimumSize(new Dimension(1120, 400));
        setMaximumSize(new Dimension(1120, 400));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel = new InputPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(inputPanel, gbc);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setPreferredSize(new Dimension(500, 80));
        navPanel.setMinimumSize(new Dimension(500, 80));
        JButton firstButton = createNavButton("First", e -> navigateToFirst());
        JButton prevButton = createNavButton("Previous", e -> navigateToPrevious());
        JButton nextButton = createNavButton("Next", e -> navigateToNext());
        JButton lastButton = createNavButton("Last", e -> navigateToLast());
        navPanel.add(firstButton);
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(lastButton);
        gbc.gridy = 1;
        add(navPanel, gbc);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setPreferredSize(new Dimension(200, 200));
        actionPanel.setMaximumSize(new Dimension(200, 200));
        actionPanel.setMinimumSize(new Dimension(200, 200));
        JButton addButton = createActionButton("Add", e -> addData());
        JButton editButton = createActionButton("Edit", e -> editData());
        JButton deleteButton = createActionButton("Delete", e -> deleteData());
        actionPanel.add(Box.createVerticalGlue());
        actionPanel.add(addButton);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(editButton);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createVerticalGlue());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(actionPanel, gbc);
    }

    public void updateInputFields(String tableName) {
        inputPanel.updateInputFields(parent.getTable(), inputFields, tableName);
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#FF9500"), 2));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private JButton createActionButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setMinimumSize(new Dimension(100, 40));
        button.setMaximumSize(new Dimension(100, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#00FF00"), 2));
        button.setOpaque(true);
        button.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void navigateToFirst() {
        if (parent.getTable().getRowCount() > 0) {
            parent.getTable().setRowSelectionInterval(0, 0);
            inputPanel.updateInputFields(parent.getTable(), inputFields, currentTableName);
        }
    }

    private void navigateToPrevious() {
        int row = parent.getTable().getSelectedRow();
        if (row > 0) {
            parent.getTable().setRowSelectionInterval(row - 1, row - 1);
            inputPanel.updateInputFields(parent.getTable(), inputFields, currentTableName);
        }
    }

    private void navigateToNext() {
        int row = parent.getTable().getSelectedRow();
        if (row < parent.getTable().getRowCount() - 1) {
            parent.getTable().setRowSelectionInterval(row + 1, row + 1);
            inputPanel.updateInputFields(parent.getTable(), inputFields, currentTableName);
        }
    }

    private void navigateToLast() {
        if (parent.getTable().getRowCount() > 0) {
            int lastRow = parent.getTable().getRowCount() - 1;
            parent.getTable().setRowSelectionInterval(lastRow, lastRow);
            inputPanel.updateInputFields(parent.getTable(), inputFields, currentTableName);
        }
    }

    private void addData() {
        if (currentTableName == null) return;
        Map<String, Object> data = new HashMap<>();
        for (String column : inputFields.keySet()) {
            String value = inputFields.get(column).getText();
            data.put(column, value.isEmpty() ? null : value);
        }
        ApiClient.addRow(currentTableName, data);
        parent.updateTableData(currentTableName);
    }

    private void editData() {
        if (currentTableName == null || parent.getTable().getSelectedRow() < 0) return;
        Map<String, Object> data = new HashMap<>();
        for (String column : inputFields.keySet()) {
            String value = inputFields.get(column).getText();
            data.put(column, value.isEmpty() ? null : value);
        }
        Object idValue = parent.getTable().getValueAt(parent.getTable().getSelectedRow(), 0);
        ApiClient.updateRow(currentTableName, parent.getTable().getColumnName(0), idValue, data);
        parent.updateTableData(currentTableName);
    }

    private void deleteData() {
        if (currentTableName == null || parent.getTable().getSelectedRow() < 0) return;
        Object idValue = parent.getTable().getValueAt(parent.getTable().getSelectedRow(), 0);
        ApiClient.deleteRow(currentTableName, parent.getTable().getColumnName(0), idValue);
        parent.updateTableData(currentTableName);
    }
}