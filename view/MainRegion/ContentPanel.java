package view.MainRegion;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import view.HomePanel;
public class ContentPanel extends JPanel {
    private HeadPanel headPanel;
    private TablePanel tablePanel;
    private HomePanel homePanel;
    private boolean isHomeDisplayed;
    private String currentTableName;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        headPanel = new HeadPanel(this::onAddButtonClicked, null);
        tablePanel = new TablePanel(this);
        homePanel = new HomePanel();
        isHomeDisplayed = false;

        headPanel.setChangeLayoutCallback(isButtonView -> {
            System.out.println("Changed");
            tablePanel.setButtonView(isButtonView);
        });

        add(headPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void onAddButtonClicked(Void ignored) {
        tablePanel.showAddFormDialog();
    }

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, Map<String, String> columnTypes, String keyColumn, String tableName, String tableComment) {
        if (isHomeDisplayed) {
            remove(homePanel);
            headPanel = new HeadPanel(this::onAddButtonClicked, tableName);
            tablePanel = new TablePanel(this); // Tạo lại TablePanel
            headPanel.setChangeLayoutCallback(isButtonView -> {
                System.out.println("Changed");
                tablePanel.setButtonView(isButtonView);
            });
            add(headPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            isHomeDisplayed = false;
        }

        this.currentTableName = tableName;

        // Định dạng ngày giờ cho các cột có kiểu datetime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter fallbackFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Map<String, String> row : data) {
            for (String column : row.keySet()) {
                String value = row.get(column);
                String dataType = columnTypes.getOrDefault(column, "unknown");
                if (value != null && dataType.equalsIgnoreCase("datetime")) {              
                    try {
                        LocalDateTime dateTime;
                        if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
                            // Định dạng yyyy-MM-dd HH:mm
                            dateTime = LocalDateTime.parse(value, fallbackFormatter);
                        } else {
                            // Định dạng yyyy-MM-dd HH:mm:ss
                            dateTime = LocalDateTime.parse(value, formatter);
                        }
                        row.put(column, dateTime.format(formatter));
                        System.out.println("Formatted datetime value for column " + column + ": " + row.get(column));
                    } catch (Exception e) {
                        System.out.println("Parse error for datetime value in column " + column + ": " + value + " → " + e.getMessage());
                    }
                }
            }
        }

        tablePanel.updateTableData(data, columnComments, columnTypes, keyColumn, tableName, tableComment);
        headPanel.updateTableNameLabel(tableComment != null && !tableComment.isEmpty() ? tableComment : tableName);

        revalidate();
        repaint();
    }

    public void showHomePanel() {
        if (!isHomeDisplayed) {
            remove(headPanel);
            remove(tablePanel);
            add(homePanel, BorderLayout.CENTER);
            isHomeDisplayed = true;
            revalidate();
            repaint();
        }
    }
}