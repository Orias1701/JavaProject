package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.ApiClient;
import model.ApiClient.TableDataResult;
import view.MainRegion.ContentPanel;
import view.MainRegion.TablePanel;
public class CheckService {
    private static final String TABLE_NAME = "e3_chitietsddv";
    
    private final ContentPanel contentPanel;
    private final TablePanel tablePanel;
    public CheckService(ContentPanel contentPanel, TablePanel tablePanel) {
        this.contentPanel = contentPanel;
        this.tablePanel = tablePanel;
    }

    public void autoProcessCheckService(String keyColumn, String keyValue) {
        try {
            // Cập nhật tổng chi phí cho việc sử dụng dịch vụ
            updateTongTienDichVu();
            
            // Lấy dữ liệu bảng từ máy chủ thông qua API
            TableDataResult result = ApiClient.getTableData(TABLE_NAME);

            // Kiểm tra nếu kết quả hoặc dữ liệu của nó là null, báo lỗi
            if (result == null || result.data == null) {
                showErrorDialog("Không thể lấy dữ liệu từ server");
                return;
            }

            // Cập nhật bảng điều khiển nội dung với dữ liệu đã lấy, bao gồm siêu dữ liệu cột
            contentPanel.updateTableData(
                result.data,           // Dữ liệu bảng
                result.columnComments, // Mô tả các cột
                result.columnTypes,   // Kiểu dữ liệu của các cột
                keyColumn,            // Cột khóa để lọc
                TABLE_NAME,           // Tên bảng
                "Chi tiết dịch vụ"    // Tiêu đề hiển thị
            );
            
            // Làm mới giao diện bảng để phản ánh dữ liệu đã cập nhật
            tablePanel.refreshTable();

            // Ghi log thông báo thành công
            System.out.println("✅ Đã cập nhật tổng tiền sử dụng dịch vụ.");
        } catch (Exception e) {
            // Hiển thị thông báo lỗi cho người dùng và ghi log ngoại lệ
            showErrorDialog("Lỗi khi xử lý: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTongTienDichVu() {
        // Câu truy vấn SQL để cập nhật tổng chi phí dựa trên số lượng dịch vụ và giá
        String query = """
            UPDATE e3_chitietsddv ct
            JOIN e1_dichvu d ON ct.MaDichVu = d.MaDichVu
            SET ct.TongTien = ct.SoLuongDichVu * d.GiaDichVu
        """;

        // Sử dụng try-with-resources để đảm bảo tài nguyên cơ sở dữ liệu được đóng
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Thực thi câu lệnh cập nhật và lấy số dòng bị ảnh hưởng
            int rowsAffected = pstmt.executeUpdate();
            // Ghi log số dòng đã được cập nhật
            System.out.println("✔ Đã cập nhật tổng tiền cho " + rowsAffected + " dòng trong bảng " + TABLE_NAME + ".");
        } catch (SQLException e) {
            // Ghi log lỗi nếu có vấn đề với cơ sở dữ liệu
            System.err.println("Lỗi khi cập nhật tổng tiền: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Ghi log lỗi nếu có vấn đề khác
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phương thức hỗ trợ để hiển thị hộp thoại lỗi cho người dùng.
     * @param message Thông báo lỗi cần hiển thị.
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
}