package view.MainRegion;

import controller.LogHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import view.Style;

public class TablePanel extends JPanel implements TableViewDataHandler {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JScrollPane scrollPane;
    private final ContentPanel parent;
    private String keyColumn;
    private String tableName;
    private String tableComment;
    private List<String> columnNames;
    private final FormDialogHandler formDialogHandler;

    public TablePanel(ContentPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setFont(Style.ROB_14);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel label) {
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                            BorderFactory.createEmptyBorder(0, 30, 0, 0)
                    ));
                    label.setFont(Style.ROB_14);
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(Style.SEC_CL);
                        label.setForeground(Style.ACT_CL);
                    } else {
                        label.setBackground(Style.LIGHT_CL);
                        label.setForeground(Style.DARK_CL);
                    }
                }
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(Style.ROB_16);
                label.setBackground(Style.MAIN_CL);
                label.setForeground(Style.LIGHT_CL);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setPreferredSize(new Dimension(label.getWidth(), 40));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                        BorderFactory.createEmptyBorder(0, 30, 0, 0)
                ));
                return label;
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        formDialogHandler = new FormDialogPanel(this);
    }

    public void showAddFormDialog() {
        if (tableName == null || tableName.isEmpty() || columnNames == null || columnNames.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn một bảng trước", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        formDialogHandler.showFormDialog("add", -1);
    }

    @Override
    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName, String tableComment) {
        this.keyColumn = keyColumn;
        this.tableName = tableName;
        this.tableComment = tableComment;
        LogHandler.logInfo("Khóa chính TablePanel: " + keyColumn);
        LogHandler.logInfo("Tên bảng TablePanel: " + tableName);
        LogHandler.logInfo("Chú thích bảng TablePanel: " + tableComment);

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            columnNames = null;
            table.revalidate();
            table.repaint();
            return;
        }

        Map<String, String> firstRow = data.get(0);
        this.columnNames = new ArrayList<>(firstRow.keySet());

        List<String> displayNames = new ArrayList<>();
        for (String columnName : columnNames) {
            String displayName = columnComments != null ? columnComments.getOrDefault(columnName, columnName) : columnName;
            displayNames.add(displayName);
        }
        displayNames.add("");
        displayNames.add("");

        tableModel.setColumnIdentifiers(displayNames.toArray());

        for (Map<String, String> row : data) {
            Object[] rowData = new Object[columnNames.size() + 2];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }
            rowData[columnNames.size()] = "Sửa";
            rowData[columnNames.size() + 1] = "Xóa";
            tableModel.addRow(rowData);
        }

        int editColumnIndex = table.getColumnCount() - 2;
        int deleteColumnIndex = table.getColumnCount() - 1;

        TableColumn editButton = table.getColumnModel().getColumn(editColumnIndex);
        TableColumn deleteButton = table.getColumnModel().getColumn(deleteColumnIndex);

        editButton.setCellRenderer(new ButtonRenderer());
        editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", formDialogHandler));
        editButton.setPreferredWidth(70);
        editButton.setMaxWidth(70);
        editButton.setMinWidth(70);
        editButton.setResizable(false);

        deleteButton.setCellRenderer(new ButtonRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JButton button) {
                    button.setForeground(Color.RED);
                }
                return c;
            }
        });
        deleteButton.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", formDialogHandler));
        deleteButton.setPreferredWidth(70);
        deleteButton.setMaxWidth(70);
        deleteButton.setMinWidth(70);
        deleteButton.setResizable(false);

        table.revalidate();
        table.repaint();
    }

    @Override
    public void refreshTable() {
        if (tableName == null || tableName.isEmpty()) {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            return;
        }
        try {
            TableDataResult result = ApiClient.getTableData(tableName);
            if (result.data != null && !result.data.isEmpty()) {
                updateTableData(result.data, result.columnComments, result.keyColumn, tableName, tableComment);
            } else {
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);
                JOptionPane.showMessageDialog(parent, "Không có dữ liệu để hiển thị sau khi làm mới", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            LogHandler.logError("Lỗi khi làm mới dữ liệu: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent, "Lỗi khi làm mới dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        table.revalidate();
        table.repaint();
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getKeyColumn() {
        return keyColumn;
    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    public JTable getTable() {
        return table;
    }
}