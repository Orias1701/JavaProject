package view.MainRegion;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import view.HomePanel;

public class ContentPanel extends JPanel {
    private HeadPanel headPanel;  // Panel hiển thị phần đầu trang (header)
    private TablePanel tablePanel;  // Panel hiển thị bảng dữ liệu
    private HomePanel homePanel;  // Panel hiển thị trang chủ
    private boolean isHomeDisplayed;  // Biến kiểm tra xem trang chủ có đang hiển thị hay không
    private String currentTableName;  // Lưu trữ tên bảng hiện tại

    public ContentPanel() {
        setLayout(new BorderLayout());  // Sử dụng BorderLayout để sắp xếp các panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Thiết lập border xung quanh panel
        setOpaque(false);  // Làm cho ContentPanel trong suốt

        // Khởi tạo các panel con
        headPanel = new HeadPanel(this::onAddButtonClicked, null);  // Tạo HeadPanel với tableName ban đầu là null
        tablePanel = new TablePanel(this);  // Tạo TablePanel với tham chiếu ContentPanel
        homePanel = new HomePanel();  // Tạo HomePanel cho trang chủ
        isHomeDisplayed = false;  // Đánh dấu không hiển thị trang chủ ban đầu

        // Thiết lập callback để thay đổi layout cho tablePanel
        headPanel.setChangeLayoutCallback(isButtonView -> {
            System.out.println("Changed");
            tablePanel.setButtonView(isButtonView);  // Cập nhật trạng thái hiển thị của tablePanel
        });

        // Thêm headPanel và tablePanel vào ContentPanel
        add(headPanel, BorderLayout.NORTH);  // Thêm headPanel vào vị trí đầu (NORTH)
        add(tablePanel, BorderLayout.CENTER);  // Thêm tablePanel vào vị trí giữa (CENTER)
    }

    // Callback khi nút thêm được nhấn
    private void onAddButtonClicked(Void ignored) {
        tablePanel.showAddFormDialog();  // Hiển thị dialog thêm mới
    }

    // Hàm cập nhật dữ liệu cho bảng
    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, Map<String, String> columnTypes, String keyColumn, String tableName, String tableComment) {
        // Nếu đang hiển thị trang chủ, cần xóa nó và cập nhật lại các panel
        if (isHomeDisplayed) {
            remove(homePanel);  // Xóa homePanel khỏi ContentPanel
            headPanel = new HeadPanel(this::onAddButtonClicked, tableName);  // Tạo lại headPanel với tableName
            tablePanel = new TablePanel(this);  // Tạo lại tablePanel mới
            // Thiết lập lại callback để đảm bảo changeLayoutCallback không bị null
            headPanel.setChangeLayoutCallback(isButtonView -> {
                System.out.println("Changed");
                tablePanel.setButtonView(isButtonView);  // Cập nhật trạng thái hiển thị của tablePanel
            });
            add(headPanel, BorderLayout.NORTH);  // Thêm lại headPanel vào ContentPanel
            add(tablePanel, BorderLayout.CENTER);  // Thêm lại tablePanel vào ContentPanel
            isHomeDisplayed = false;  // Đánh dấu không còn hiển thị trang chủ
        }

        // Lưu tên bảng hiện tại
        this.currentTableName = tableName;

        // Định dạng ngày giờ theo yêu cầu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Map<String, String> row : data) {
            for (String column : row.keySet()) {
                String value = row.get(column);
                try {
                    if (value != null && value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}(:\\d{2})?")) {
                        if (value.length() == 16) value += ":00"; // nếu thiếu giây
                        LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
                        row.put(column, dateTime.format(formatter));
                        System.out.println("Formatted value: " + row.get(column));
                    }
                } catch (Exception e) {
                    System.out.println("Parse error for value: " + value + " → " + e.getMessage());
                }
            }
        }

        // Cập nhật dữ liệu cho bảng và tiêu đề
        tablePanel.updateTableData(data, columnComments, columnTypes, keyColumn, tableName, tableComment);
        headPanel.updateTableNameLabel(tableComment != null && !tableComment.isEmpty() ? tableComment : tableName);

        // Cập nhật lại giao diện sau khi thay đổi
        revalidate();  // Cập nhật lại layout
        repaint();  // Vẽ lại giao diện
    }

    // Hàm hiển thị trang chủ
    public void showHomePanel() {
        // Kiểm tra nếu trang chủ chưa được hiển thị
        if (!isHomeDisplayed) {
            remove(headPanel);  // Xóa headPanel khỏi ContentPanel
            remove(tablePanel);  // Xóa tablePanel khỏi ContentPanel
            add(homePanel, BorderLayout.CENTER);  // Thêm homePanel vào vị trí trung tâm
            isHomeDisplayed = true;  // Đánh dấu đang hiển thị trang chủ
            revalidate();  // Cập nhật lại giao diện
            repaint();  // Vẽ lại giao diện
        }
    }
}