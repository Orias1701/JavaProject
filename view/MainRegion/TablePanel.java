package view.MainRegion;

import controller.LogHandler;
import controller.UserSession;
import java.awt.*;
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
    private String tableName;
    private String tableComment;
    private java.util.List<String> columnNames;
    private java.util.List<String> columnComments;
    private java.util.List<String> columnTypes;
    private FormDialogHandler formDialogHandler;
    private TableView tableView;
    private GridView gridView;
    private JPanel currentView;

    public TablePanel(ContentPanel parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        tableView = new TableView();
        // Initialize views
        gridView = new GridView(this);
        currentView = tableView.getView();

        // Initialize JScrollPane
        scrollPane = new JScrollPane(currentView);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        // Customize scrollbar
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
    public void updateTableData(java.util.List<java.util.Map<String, String>> data, java.util.Map<String, String> columnCommentsMap, java.util.Map<String, String> columnTypesMap,String keyColumn, String tableName, String tableComment) {
        this.keyColumn = keyColumn;
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.columnNames = data != null && !data.isEmpty() ? new java.util.ArrayList<>(data.get(0).keySet()) : null;
        this.columnComments = new java.util.ArrayList<>();
        
        if (columnNames != null) {
            for (String columnName : columnNames) {
                String comment = columnCommentsMap != null ? columnCommentsMap.getOrDefault(columnName, columnName) : columnName;
                this.columnComments.add(comment);
            }
        }
        
        LogHandler.logInfo("Khóa chính TablePanel: " + keyColumn);
        LogHandler.logInfo("Tên bảng TablePanel: " + tableName);
        LogHandler.logInfo("Chú thích bảng TablePanel: " + tableComment);

        boolean canAdd = UserSession.hasPermission(tableName, "10");
        boolean canEdit = UserSession.hasPermission(tableName, "20");
        boolean canDelete = UserSession.hasPermission(tableName, "30");

        if (isButtonView) {
            gridView.updateView(data, columnNames, columnComments, formDialogHandler, canAdd, canEdit, canDelete);
        } else {
            tableView.updateView(data, columnNames, columnComments, formDialogHandler, canAdd, canEdit, canDelete);
        }
        
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
                updateTableData(result.data, result.columnComments, result.columnTypes,result.keyColumn, tableName, tableComment);
            } else {
                updateTableData(null, null, null, null, tableName, tableComment);
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

    @Override
    public java.util.List<String> getColumnNames() {
        return columnNames;
    }

    public java.util.List<String> getColumnComments() {
        return columnComments;
    }

    public java.util.List<String> getColumnTypes(){
        return columnTypes;
    }
    public JTable getTable() {
        return tableView.getTable();
    }
}