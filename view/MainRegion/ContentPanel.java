package view.MainRegion;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;
import view.HomePanel;

public class ContentPanel extends JPanel {
    private HeadPanel headPanel;
    private TablePanel tablePanel;
    private HomePanel homePanel;
    private boolean isHomeDisplayed;
    // private boolean isButtonView = false;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        headPanel = new HeadPanel(this::onAddButtonClicked);
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

    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName, String tableComment) {

        if (isHomeDisplayed) {
            remove(homePanel);
            headPanel = new HeadPanel(this::onAddButtonClicked);
            tablePanel = new TablePanel(this);
            // Thiết lập lại callback để đảm bảo changeLayoutCallback không bị null
            headPanel.setChangeLayoutCallback(isButtonView -> {
                System.out.println("Changed");
                tablePanel.setButtonView(isButtonView);
            });
            add(headPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            isHomeDisplayed = false;
        }

        tablePanel.updateTableData(data, columnComments, keyColumn, tableName, tableComment);
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