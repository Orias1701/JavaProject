package view.MainRegion;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import view.HomePanel;

public class ContentPanel extends JPanel {
    private HeadPanel headPanel;  // Panel hiển thị phần đầu trang (header)
    private TablePanel tablePanel;  // Panel hiển thị bảng dữ liệu
    private HomePanel homePanel;  // Panel hiển thị trang chủ
    private boolean isHomeDisplayed;  // Biến kiểm tra xem trang chủ có đang hiển thị hay không

    // Constructor ContentPanel
    public ContentPanel() {
        setLayout(new BorderLayout());  // Sử dụng BorderLayout để sắp xếp các panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Thiết lập border xung quanh panel
        setOpaque(false);  // Làm cho ContentPanel trong suốt

        // Khởi tạo các panel con
        headPanel = new HeadPanel(this::onAddButtonClicked);  // Tạo HeadPanel với callback cho nút thêm
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
    public void updateTableData(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn, String tableName, String tableComment) {

        // Nếu đang hiển thị trang chủ, cần xóa nó và cập nhật lại các panel
        if (isHomeDisplayed) {
            remove(homePanel);  // Xóa homePanel khỏi ContentPanel
            headPanel = new HeadPanel(this::onAddButtonClicked);  // Tạo lại headPanel mới
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

        // Cập nhật dữ liệu cho bảng và tiêu đề
        tablePanel.updateTableData(data, columnComments, keyColumn, tableName, tableComment);
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
