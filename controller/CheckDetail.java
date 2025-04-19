package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import model.ApiClient;
import model.ApiClient.ApiResponse;
import model.TableDataOperationsClient;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;
public class CheckDetail {

    private final TableDataOperationsClient client;
    private ContentPanel contentPanel; // Tham chiếu đến ContentPanel để cập nhật dữ liệu
    // Constructor để khởi tạo với authToken
    // CheckDetail.java
    private TablePanel tablePanel; // Declare TablePanel instance

    public CheckDetail(String authToken, ContentPanel contentPanel, TablePanel tablePanel) {
        this.client = new TableDataOperationsClient(authToken);
        this.contentPanel = contentPanel; 
        this.tablePanel = tablePanel; // Initialize TablePanel
    }


    // Hàm chính xử lý logic kiểm tra và cập nhật đền bù
    public ApiResponse processCheckDetail(String tableName, String keyColumn, String keyValue) {
        ApiResponse res;
        
        if (keyValue == null || keyValue.trim().isEmpty()) {
            // Nếu không có keyValue, lấy tất cả dữ liệu
            res = client.getRow(tableName, keyColumn, keyValue); // Giả sử phương thức này lấy tất cả dòng trong bảng
        } else {
            // Nếu có keyValue, truy vấn theo key
            res = client.getRow(tableName, keyColumn, keyValue);
        }
    
        if (res.isSuccess()) {
            System.out.println("✅ Dữ liệu dòng:");
            System.out.println(res.getMessage());
    
            updateDenBuTheoTinhTrang(); // Cập nhật đền bù theo tình trạng thiết bị
            tablePanel.refreshTable();
        } else {
            System.err.println("❌ Lỗi: " + res.getMessage());
        }
    
        return res;
    }
    

    // Hàm cập nhật cột Đền Bù theo tình trạng thiết bị
    private void updateDenBuTheoTinhTrang() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Trường hợp 'Tốt' => Đền Bù = 0
            String updateTot = "UPDATE d4_kiemtrachitiet SET DenBu = 0 WHERE TinhTrang = 'Tốt'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateTot)) {
                int rows = pstmt.executeUpdate();
                System.out.println("✔ Đã cập nhật " + rows + " dòng có Tình Trạng = 'Tốt' (Đền Bù = 0)");
            }

            // Trường hợp 'Hỏng' => Lấy Đền Bù từ bảng a7_thietbi
            String updateHong = """
                UPDATE d4_kiemtrachitiet kt
                JOIN d1_thietbi tb ON kt.MaThietBi = tb.MaThietBi
                SET kt.DenBu = tb.DenBu
                WHERE kt.TinhTrang = 'Hỏng'
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(updateHong)) {
                int rows = pstmt.executeUpdate();
                System.out.println("✔ Đã cập nhật " + rows + " dòng có Tình Trạng = 'Hỏng' (Lấy Đền Bù từ bảng thiết bị)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm xử lý tự động kiểm tra và cập nhật đền bù
    public void autoProcessCheckDetail(String tableName, String keyColumn, String keyValue) {
        try {
            processCheckDetail(tableName, keyColumn, keyValue);
            // ✅ Lấy lại dữ liệu sau khi cập nhật
            ApiClient.TableDataResult result = ApiClient.getTableData(tableName);
            if (result == null || result.data == null) {
                JOptionPane.showMessageDialog(null, "Không thể lấy dữ liệu từ server", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // ✅ Lấy lại cột khóa chính
            updateDenBuTheoTinhTrang();
            // ✅ Cập nhật bảng hiển thị
            contentPanel.updateTableData(result.data, result.columnComments, keyColumn, tableName, "Chi tiết kiểm tra");
    
            System.out.println("Done check detail");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xử lý: " + e.getMessage(), "Lỗi check detail", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
