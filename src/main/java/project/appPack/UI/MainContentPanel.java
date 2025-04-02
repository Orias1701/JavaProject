package project.appPack.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MainContentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private String currentTableName;
    private String idColumn;
    private Map<String, JTextField> inputFields;
    private int currentRowIndex = -1;

    public MainContentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // JPanel chính để chứa tất cả
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 1. Trên cùng: Tên bảng
        JLabel tableNameLabel = new JLabel("No table selected");
        tableNameLabel.setFont(tableNameLabel.getFont().deriveFont(20f));
        tableNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        add(tableNameLabel, BorderLayout.NORTH);

        // 2. Phần công cụ (chiều cao cố định)
        JPanel toolPanel = new JPanel(new GridBagLayout());
        toolPanel.setPreferredSize(new Dimension(1280, 480));
        toolPanel.setMinimumSize(new Dimension(1280, 480));
        toolPanel.setMaximumSize(new Dimension(1280, 480));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputFields = new HashMap<>();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Ô nhập liệu (bên trái) với thanh cuộn
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(null); // Bỏ viền cho inputPanel
        JScrollPane inputScrollPane = new JScrollPane(inputPanel);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setPreferredSize(new Dimension(500, 300));
        inputScrollPane.setMinimumSize(new Dimension(500, 300));
        inputScrollPane.setBorder(null); // Bỏ viền cho inputScrollPane
        customizeScrollBar(inputScrollPane);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        toolPanel.add(inputScrollPane, gbc);

        // Nút điều hướng (giữa)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setPreferredSize(new Dimension(500, 50));
        navPanel.setMinimumSize(new Dimension(500, 50));
        JButton firstButton = createNavButton("First");
        JButton prevButton = createNavButton("Previous");
        JButton nextButton = createNavButton("Next");
        JButton lastButton = createNavButton("Last");
        navPanel.add(firstButton);
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(lastButton);
        gbc.gridy = 1;
        toolPanel.add(navPanel, gbc);

        // Nút hành động (bên phải)
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setPreferredSize(new Dimension(200, 200));
        actionPanel.setMaximumSize(new Dimension(200, 200));
        actionPanel.setMinimumSize(new Dimension(200, 200));
        JButton addButton = createActionButton("Add");
        JButton editButton = createActionButton("Edit");
        JButton deleteButton = createActionButton("Delete");
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
        toolPanel.add(actionPanel, gbc);

        mainPanel.add(toolPanel, BorderLayout.NORTH); // Đặt toolPanel ở NORTH để cố định chiều cao

        // 3. Phần bảng (chiều cao linh hoạt)
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    currentRowIndex = selectedRow;
                    updateInputFields();
                }
            }
        });

        // Tăng kích thước cell
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setShowGrid(false); // Tắt viền lưới đen mặc định

        // Tùy chỉnh header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#FF9500"));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Tùy chỉnh viền và renderer cho các cell
        Border lightOrangeBorder = BorderFactory.createLineBorder(Color.decode("#FFB580"), 1);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 14));
                setBorder(lightOrangeBorder);
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Tùy chỉnh viền cho header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.decode("#FF9500"));
                c.setFont(new Font("Arial", Font.BOLD, 14));
                setBorder(lightOrangeBorder);
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(table);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Sự kiện điều hướng và hành động
        firstButton.addActionListener(e -> navigateToFirst());
        prevButton.addActionListener(e -> navigateToPrevious());
        nextButton.addActionListener(e -> navigateToNext());
        lastButton.addActionListener(e -> navigateToLast());
        addButton.addActionListener(e -> addData());
        editButton.addActionListener(e -> editData());
        deleteButton.addActionListener(e -> deleteData());

        // Cập nhật tên bảng và input fields khi chọn bảng
        this.addPropertyChangeListener("currentTableName", evt -> {
            tableNameLabel.setText(currentTableName);
            updateInputPanel(inputPanel);
        });
    }

    public void updateTableData(String tableName) {
        currentTableName = tableName;
        firePropertyChange("currentTableName", null, tableName);
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        List<Map<String, Object>> tableData = ApiClient.getTableData(tableName);
        if (tableData == null || tableData.isEmpty()) {
            tableModel.addColumn("Message");
            tableModel.addRow(new Object[]{"No data available"});
            return;
        }

        Map<String, Object> firstRow = tableData.get(0);
        String[] columnNames = firstRow.keySet().toArray(String[]::new);
        idColumn = columnNames[0];
        tableModel.setColumnIdentifiers(columnNames);

        for (Map<String, Object> row : tableData) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                rowData[i] = row.get(columnNames[i]);
            }
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            currentRowIndex = 0;
            table.setRowSelectionInterval(0, 0);
            updateInputFields();
        }
    }

    private void updateInputPanel(JPanel inputPanel) {
        inputPanel.removeAll();
        inputFields.clear();

        if (currentTableName == null) return;

        String[] columnNames = new String[tableModel.getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = tableModel.getColumnName(i);
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

        inputPanel.revalidate();
        inputPanel.repaint();

        if (currentRowIndex >= 0) updateInputFields();
    }

    private void updateInputFields() {
        if (currentRowIndex < 0 || currentRowIndex >= tableModel.getRowCount()) return;

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            String columnName = tableModel.getColumnName(i);
            Object value = tableModel.getValueAt(currentRowIndex, i);
            inputFields.get(columnName).setText(value != null ? value.toString() : "");
        }
    }

    private void navigateToFirst() {
        if (tableModel.getRowCount() > 0) {
            currentRowIndex = 0;
            table.setRowSelectionInterval(0, 0);
            updateInputFields();
        }
    }

    private void navigateToPrevious() {
        if (currentRowIndex > 0) {
            currentRowIndex--;
            table.setRowSelectionInterval(currentRowIndex, currentRowIndex);
            updateInputFields();
        }
    }

    private void navigateToNext() {
        if (currentRowIndex < tableModel.getRowCount() - 1) {
            currentRowIndex++;
            table.setRowSelectionInterval(currentRowIndex, currentRowIndex);
            updateInputFields();
        }
    }

    private void navigateToLast() {
        if (tableModel.getRowCount() > 0) {
            currentRowIndex = tableModel.getRowCount() - 1;
            table.setRowSelectionInterval(currentRowIndex, currentRowIndex);
            updateInputFields();
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
        updateTableData(currentTableName);
    }

    private void editData() {
        if (currentTableName == null || currentRowIndex < 0 || currentRowIndex >= tableModel.getRowCount()) return;

        Map<String, Object> data = new HashMap<>();
        for (String column : inputFields.keySet()) {
            String value = inputFields.get(column).getText();
            data.put(column, value.isEmpty() ? null : value);
        }

        Object idValue = tableModel.getValueAt(currentRowIndex, 0);
        ApiClient.updateRow(currentTableName, idColumn, idValue, data);
        updateTableData(currentTableName);
    }

    private void deleteData() {
        if (currentTableName == null || currentRowIndex < 0 || currentRowIndex >= tableModel.getRowCount()) return;

        Object idValue = tableModel.getValueAt(currentRowIndex, 0);
        ApiClient.deleteRow(currentTableName, idColumn, idValue);
        updateTableData(currentTableName);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#FF9500"), 2));
        button.setOpaque(true);
        button.setFocusPainted(false);
        return button;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setMinimumSize(new Dimension(100, 40));
        button.setMaximumSize(new Dimension(100, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#00FF00"), 2));
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setFocusPainted(false);
        return button;
    }

    private void customizeScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();

        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.decode("#D3D3D3");
                this.trackColor = Color.WHITE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        verticalBar.setPreferredSize(new Dimension(8, 0));

        horizontalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.decode("#D3D3D3");
                this.trackColor = Color.WHITE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        horizontalBar.setPreferredSize(new Dimension(0, 8));
    }
}