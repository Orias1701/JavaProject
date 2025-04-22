package view.MainRegion;

import controller.LogHandler;
import controller.UserSession;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import view.Style;

public class TablePanel extends JPanel implements TableViewDataHandler {
    
    private final JScrollPane scrollPane;
    private final ContentPanel parent;
    private boolean isButtonView = false;
    private String keyColumn;
    private List<String> primaryKeyColumns; // Danh sách các cột khóa chính
    private String tableName;
    private String tableComment;
    private List<String> columnNames;
    private List<String> columnComments;
    private List<String> columnTypes; // Danh sách kiểu dữ liệu cột
    private FormDialogHandler formDialogHandler;
    private TableView tableView;
    private GridView gridView;
    private JPanel currentView;
    private List<Map<String, String>> currentData; // Lưu dữ liệu hiện tại

    public TablePanel(ContentPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        tableView = new TableView(this);
        gridView = new GridView(this);
        currentView = tableView.getView();

        scrollPane = new JScrollPane(currentView);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new Style.CustomScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(13, Integer.MAX_VALUE));
        verticalScrollBar.setUnitIncrement(20);
        verticalScrollBar.setOpaque(true);
        verticalScrollBar.setBackground(Style.LIGHT_CL);
        verticalScrollBar.setBorder(new EmptyBorder(0, 5, 0, 0));

        add(scrollPane, BorderLayout.CENTER);

        formDialogHandler = new FormDialogPanel(this);
    }

    public void setButtonView(boolean isButtonView) {
        if (this.isButtonView != isButtonView) {
            this.isButtonView = isButtonView;
            currentView = isButtonView ? gridView.getView() : tableView.getView();
            scrollPane.setViewportView(currentView);
            scrollPane.revalidate();
            scrollPane.repaint();
            refreshTable();
        }
    }

    public void showAddFormDialog() {
        if (tableName == null || tableName.isEmpty() || columnNames == null || columnNames.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn một bảng trước", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        formDialogHandler.showFormDialog("add", -1);
    }

    @Override
    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnCommentsMap, 
                               Map<String, String> columnTypesMap, String keyColumn, 
                               String tableName, String tableComment) {
        // Chuyển tiếp đến phương thức mới với danh sách primaryKeyColumns rỗng
        updateTableData(data, columnCommentsMap, columnTypesMap, keyColumn, new ArrayList<>(), tableName, tableComment);
    }

    // Phương thức nạp chồng mới với primaryKeyColumns (không có @Override)
    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnCommentsMap, 
                               Map<String, String> columnTypesMap, String keyColumn, List<String> primaryKeyColumns, 
                               String tableName, String tableComment) {
        this.keyColumn = keyColumn;
        this.primaryKeyColumns = primaryKeyColumns;
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.currentData = data;
        this.columnNames = data != null && !data.isEmpty() ? new ArrayList<>(data.get(0).keySet()) : null;
        this.columnComments = new ArrayList<>();
        this.columnTypes = new ArrayList<>();

        if (columnNames != null) {
            for (String columnName : columnNames) {
                String comment = columnCommentsMap != null ? columnCommentsMap.getOrDefault(columnName, columnName) : columnName;
                String type = columnTypesMap != null ? columnTypesMap.getOrDefault(columnName, "unknown") : "unknown";
                this.columnComments.add(comment);
                this.columnTypes.add(type);
            }
        }
        
        LogHandler.logInfo("Khóa chính TablePanel: " + keyColumn);
        LogHandler.logInfo("Các cột khóa chính TablePanel: " + primaryKeyColumns);
        LogHandler.logInfo("Tên bảng TablePanel: " + tableName);
        LogHandler.logInfo("Chú thích bảng TablePanel: " + tableComment);
        LogHandler.logInfo("Kiểu dữ liệu cột: " + columnTypes);

        boolean canAdd = UserSession.hasPermission(tableName, "10");
        boolean canEdit = UserSession.hasPermission(tableName, "20");
        boolean canDelete = UserSession.hasPermission(tableName, "30");

        // Tạo FormDialogHandler mới để đồng bộ với bảng hiện tại
        this.formDialogHandler = new FormDialogPanel(this);

        // Cập nhật TableView và GridView (tạm thời dùng chữ ký hiện tại, cần cập nhật sau)
        // TODO: Cập nhật TableView.java và GridView.java để chấp nhận primaryKeyColumns
        tableView.updateView(data, columnNames, columnComments, formDialogHandler, canAdd, canEdit, canDelete);
        gridView.updateView(data, columnNames, columnComments, columnTypes, formDialogHandler, canAdd, canEdit, canDelete);

        // Hiển thị view đúng
        if (isButtonView) {
            currentView = gridView.getView();
        } else {
            currentView = tableView.getView();
        }
        scrollPane.setViewportView(currentView);
        
        currentView.revalidate();
        currentView.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    @Override
    public void refreshTable() {
        if (tableName == null || tableName.isEmpty()) {
            updateTableData(null, null, null, null, tableName, tableComment);
            return;
        }
        try {
            TableDataResult result = ApiClient.getTableData(tableName);
            if (result.data != null && !result.data.isEmpty()) {
                updateTableData(result.data, result.columnComments, result.columnTypes, 
                               result.keyColumn, result.primaryKeyColumns, tableName, tableComment);
            } else {
                updateTableData(null, null, null, null, new ArrayList<>(), tableName, tableComment);
                JOptionPane.showMessageDialog(parent, "Không có dữ liệu để hiển thị sau khi làm mới", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            LogHandler.logError("Lỗi khi làm mới dữ liệu: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent, "Lỗi khi làm mới dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getKeyColumn() {
        return keyColumn;
    }

    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<String> getColumnComments() {
        return columnComments;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public JTable getTable() {
        return tableView.getTable();
    }

    ContentPanel getContentPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}