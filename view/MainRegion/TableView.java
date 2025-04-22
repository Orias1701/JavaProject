package view.MainRegion;

import controller.BillHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;
import view.Style;

/**
 * TableView là lớp hiển thị bảng dữ liệu có hỗ trợ nút thao tác (Sửa/Xóa/Chi tiết),
 * định dạng theo kiểu dữ liệu (ví dụ: căn lề phải cho decimal, định dạng ngày giờ...).
 */
public class TableView {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JPanel view;
    private final TablePanel parent; // Dùng để truy xuất danh sách cột và kiểu dữ liệu

    public TableView(TablePanel parent) {
        this.parent = parent;
        view = new JPanel(new BorderLayout());
        view.setOpaque(true);
        view.setBackground(Style.LIGHT_CL);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setFont(Style.ROB_14);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Tùy chỉnh hiển thị từng ô trong bảng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel label) {
                    // Set viền và padding ô
                    label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                        BorderFactory.createEmptyBorder(0, 20, 0, 15)
                    ));
                    label.setFont(Style.ROB_14);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? Style.SEC_CL : Style.LIGHT_CL);
                    label.setForeground(isSelected ? Style.ACT_CL : Style.DARK_CL);

                    // Lấy thông tin về kiểu dữ liệu để căn lề
                    List<String> columnTypes = parent.getColumnTypes();
                    List<String> columnNames = parent.getColumnNames();
                    List<String> columnComments = parent.getColumnComments();
                    String columnName = table.getColumnName(column);

                    int columnIndex = columnComments != null ? columnComments.indexOf(columnName) : -1;
                    if (columnIndex < 0 && columnNames != null)
                        columnIndex = columnNames.indexOf(columnName);

                    // Căn lề phải nếu là kiểu decimal, trái nếu không
                    if (columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                        String dataType = columnTypes.get(columnIndex);
                        label.setHorizontalAlignment(dataType.equalsIgnoreCase("decimal") ? SwingConstants.RIGHT : SwingConstants.LEFT);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    // Định dạng lại chuỗi datetime nếu có
                    if (value instanceof String str && columnIndex >= 0 && columnTypes != null) {
                        String dataType = columnTypes.get(columnIndex);
                        if (dataType.equalsIgnoreCase("datetime") || dataType.equalsIgnoreCase("timestamp")) {
                            try {
                                java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
                                label.setText(new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));
                            } catch (java.text.ParseException e1) {
                                try {
                                    java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str);
                                    label.setText(new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));
                                } catch (java.text.ParseException e2) {
                                    try {
                                        java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd HH").parse(str);
                                        label.setText(new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));
                                    } catch (java.text.ParseException e3) {
                                        label.setText(str); // Không định dạng được → để nguyên
                                    }
                                }
                            }
                        }
                    }
                }

                return c;
            }
        });

        // Tùy chỉnh phần tiêu đề cột
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(Style.ROB_16);
                label.setBackground(Style.MAIN_CL);
                label.setForeground(Style.LIGHT_CL);
                label.setPreferredSize(new Dimension(label.getWidth(), 40));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 1, 1, Style.LIGHT_CL),
                    BorderFactory.createEmptyBorder(0, 20, 0, 15)
                ));

                // Căn lề tiêu đề theo kiểu dữ liệu
                List<String> columnTypes = parent.getColumnTypes();
                List<String> columnComments = parent.getColumnComments();
                String columnName = table.getColumnName(column);
                int columnIndex = columnComments != null ? columnComments.indexOf(columnName) : -1;
                if (columnIndex < 0 && parent.getColumnNames() != null) {
                    columnIndex = parent.getColumnNames().indexOf(columnName);
                }

                if (columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                    String dataType = columnTypes.get(columnIndex);
                    label.setHorizontalAlignment(dataType.equalsIgnoreCase("decimal") ? SwingConstants.RIGHT : SwingConstants.LEFT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }

                return label;
            }
        });

        // Thêm header và bảng vào panel view
        view.add(table.getTableHeader(), BorderLayout.NORTH);
        view.add(table, BorderLayout.CENTER);
    }

    public JPanel getView() {
        return view;
    }

    public JTable getTable() {
        return table;
    }

    /**
     * Cập nhật dữ liệu cho bảng từ danh sách Map (key là tên cột, value là giá trị),
     * thêm nút Sửa/Xóa/Chi tiết tùy theo quyền.
     */
    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, List<String> primaryKeyColumns, FormDialogHandler formDialogHandler, boolean canAdd, boolean canEdit, boolean canDelete) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            table.revalidate();
            table.repaint();
            return;
        }

        // Hiển thị tên cột và thêm cột chức năng
        List<String> displayNames = new ArrayList<>(columnComments);
        displayNames.add(""); // Cột Sửa hoặc Chi tiết
        if (canDelete) {
            displayNames.add(""); // Cột Xóa nếu có
        }

        tableModel.setColumnIdentifiers(displayNames.toArray());

        for (Map<String, String> row : data) {
            Object[] rowData = new Object[displayNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
                rowData[i] = row.get(columnNames.get(i));
            }

            rowData[columnNames.size()] = canEdit ? "Sửa" : "Chi tiết";
            if (canDelete) {
                rowData[columnNames.size() + 1] = "Xóa";
            }

            tableModel.addRow(rowData);
        }
        
        // Cài đặt nút Sửa hoặc Chi tiết
        int editCol = canDelete ? table.getColumnCount() - 2 : table.getColumnCount() - 1;
        TableColumn editButton = table.getColumnModel().getColumn(editCol);
        editButton.setCellRenderer(new ButtonRenderer());
        if (canEdit) {
            editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "edit", formDialogHandler));
        } else {
            editButton.setCellEditor(new ButtonEditor(new JCheckBox(), "detail", formDialogHandler));
        }
        editButton.setPreferredWidth(canEdit ? 70 : 120);
        editButton.setMaxWidth(canEdit ? 70 : 120);
        editButton.setMinWidth(canEdit ? 70 : 120);
        editButton.setResizable(false);
        
        // Cài đặt nút Xóa nếu có
        if (canDelete) {
            int deleteCol = table.getColumnCount() - 1;
            TableColumn deleteButton = table.getColumnModel().getColumn(deleteCol);
            deleteButton.setCellRenderer(new ButtonRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (c instanceof JButton btn) {
                        btn.setForeground(Color.RED); // Đổi màu chữ nút Xóa
                    }
                    return c;
                }
            });
            deleteButton.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", formDialogHandler));
            deleteButton.setPreferredWidth(70);
            deleteButton.setMaxWidth(70);
            deleteButton.setMinWidth(70);
            deleteButton.setResizable(false);
        }

        table.revalidate();
        table.repaint();
    } 
    class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String action;
    private boolean isPushed;
    private FormDialogHandler formDialogHandler;
    private int selectedRow;

    public ButtonEditor(JCheckBox checkBox, String action, FormDialogHandler formDialogHandler) {
        super(checkBox);
        this.action = action;
        this.formDialogHandler = formDialogHandler;
        button = new JButton(action.equals("edit") ? "Sửa" : action.equals("delete") ? "Xóa" : "Chi tiết");
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        button.setText(value != null ? value.toString() : "");
        isPushed = true;
        selectedRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed && selectedRow >= 0) {
            try {
                if ("edit".equals(action)) {
                    if (formDialogHandler != null) {
                        formDialogHandler.showFormDialog("edit", selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(null, "FormDialogHandler chưa được khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("detail".equals(action)) {
                    String maHoaDon = table.getValueAt(selectedRow, 0).toString();
                    BillHandler billHandler = new BillHandler(parent.getContentPanel(), parent);
                    Window window = SwingUtilities.getWindowAncestor(parent);
                    if (window instanceof Frame frame) {
                        billHandler.showInvoiceDetail(frame, maHoaDon);
                    } else {
                        JOptionPane.showMessageDialog(null, "Parent window is not a Frame!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("delete".equals(action)) {
                    if (formDialogHandler != null) {
                        formDialogHandler.showFormDialog("delete", selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(null, "FormDialogHandler chưa được khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi thực hiện hành động: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một hàng để thực hiện hành động!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        isPushed = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}

}