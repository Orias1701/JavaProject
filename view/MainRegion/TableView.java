package view.MainRegion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;
import view.Style;

public class TableView {
    private final JTable table; // Bảng hiển thị dữ liệu
    private final DefaultTableModel tableModel; // Model dữ liệu cho bảng
    private final JPanel view; // Panel chứa bảng
    private final TablePanel parent; // Panel cha để lấy thông tin cột

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

        // Renderer mặc định cho tất cả cell trong bảng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel label) {
                    // Tạo viền và style cho mỗi ô
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Style.ACT_CL),
                            BorderFactory.createEmptyBorder(0, 20, 0, 15)
                    ));
                    label.setFont(Style.ROB_14);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? Style.SEC_CL : Style.LIGHT_CL);
                    label.setForeground(isSelected ? Style.ACT_CL : Style.DARK_CL);

                    // Lấy thông tin kiểu dữ liệu để căn lề phù hợp
                    List<String> columnTypes = parent.getColumnTypes();
                    List<String> columnNames = parent.getColumnNames();
                    List<String> columnComments = parent.getColumnComments();
                    String columnName = table.getColumnName(column);

                    // Xác định index của cột đang render
                    int columnIndex = columnComments != null ? columnComments.indexOf(columnName) : -1;
                    if (columnIndex < 0 && columnNames != null) {
                        columnIndex = columnNames.indexOf(columnName);
                    }

                    // Căn phải nếu kiểu dữ liệu là số (decimal)
                    if (columnIndex >= 0 && columnTypes != null && columnIndex < columnTypes.size()) {
                        String dataType = columnTypes.get(columnIndex);
                        if (dataType.equalsIgnoreCase("decimal")) {
                            label.setHorizontalAlignment(SwingConstants.RIGHT);
                        } else {
                            label.setHorizontalAlignment(SwingConstants.LEFT);
                        }
                    }

                    // Xử lý định dạng ngày giờ nếu dữ liệu kiểu datetime/timestamp
                    if (value instanceof String str && columnIndex >= 0 &&
                        columnTypes != null && columnIndex < columnTypes.size()) {

                        String dataType = columnTypes.get(columnIndex);
                        if (dataType.equalsIgnoreCase("datetime") || dataType.equalsIgnoreCase("timestamp")) {
                            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                            // Thử nhiều định dạng ngày đầu vào
                            String[] formats = {
                                "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH"
                            };

                            for (String fmt : formats) {
                                try {
                                    java.util.Date date = new java.text.SimpleDateFormat(fmt).parse(str);
                                    label.setText(outputFormat.format(date));
                                    break;
                                } catch (java.text.ParseException ignored) {}
                            }
                        }
                    }
                }
                return c;
            }
        });

        // Renderer cho tiêu đề bảng (header)
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

        // Thêm bảng vào view
        view.add(table.getTableHeader(), BorderLayout.NORTH);
        view.add(table, BorderLayout.CENTER);
    }

    // Trả về JPanel chứa bảng
    public JPanel getView() {
        return view;
    }

    // Trả về đối tượng JTable
    public JTable getTable() {
        return table;
    }

    /**
     * Cập nhật bảng hiển thị dữ liệu từ danh sách Map (mỗi Map là 1 dòng)
     */
    public void updateView(List<Map<String, String>> data, List<String> columnNames, List<String> columnComments, FormDialogHandler formDialogHandler, boolean canAdd, boolean canEdit, boolean canDelete) {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            table.revalidate();
            table.repaint();
            return;
        }

        // Thêm tiêu đề cột
        List<String> displayNames = new ArrayList<>(columnComments);
        displayNames.add(""); // Cột cho nút Sửa/Chi tiết
        if (canDelete) {
            displayNames.add(""); // Cột cho nút Xóa nếu được phép
        }
        tableModel.setColumnIdentifiers(displayNames.toArray());

        // Thêm dữ liệu từng dòng
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

        // Cài đặt button "Sửa" hoặc "Chi tiết"
        int actionColIndex = table.getColumnCount() - (canDelete ? 2 : 1);
        TableColumn actionCol = table.getColumnModel().getColumn(actionColIndex);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(new JCheckBox(), canEdit ? "edit" : "detail", formDialogHandler));
        actionCol.setPreferredWidth(canEdit ? 70 : 120);
        actionCol.setMaxWidth(canEdit ? 70 : 120);
        actionCol.setMinWidth(canEdit ? 70 : 120);
        actionCol.setResizable(false);

        // Cài đặt button "Xóa" nếu có
        if (canDelete) {
            int deleteColIndex = table.getColumnCount() - 1;
            TableColumn deleteCol = table.getColumnModel().getColumn(deleteColIndex);
            deleteCol.setCellRenderer(new ButtonRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (c instanceof JButton button) {
                        button.setForeground(Color.RED); // Nút xóa có màu đỏ
                    }
                    return c;
                }
            });
            deleteCol.setCellEditor(new ButtonEditor(new JCheckBox(), "delete", formDialogHandler));
            deleteCol.setPreferredWidth(70);
            deleteCol.setMaxWidth(70);
            deleteCol.setMinWidth(70);
            deleteCol.setResizable(false);
        }
        
        // Cập nhật lại bảng
        table.revalidate();
        table.repaint();
    }
}
