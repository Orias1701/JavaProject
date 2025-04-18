package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.ApiClient.ApiResponse;
import model.TableDataOperationsClient;

public class CheckDetail {

    private final TableDataOperationsClient client;

    // Constructor để khởi tạo với authToken
    public CheckDetail(String authToken) {
        this.client = new TableDataOperationsClient(authToken);
    }

    // Hàm chính xử lý logic kiểm tra và cập nhật đền bù
    public ApiResponse processCheckDetail(String tableName, String keyColumn, String keyValue) {
        ApiResponse res = client.getRow(tableName, keyColumn, keyValue);

        if (res.isSuccess()) {
            System.out.println("✅ Dữ liệu dòng:");
            System.out.println(res.getMessage());

            updateDenBuTheoTinhTrang(); // Cập nhật đền bù theo tình trạng thiết bị
        } else {
            System.err.println("❌ Lỗi: " + res.getMessage());
        }

        return res;
    }

    // Hàm cập nhật cột Đền Bù theo tình trạng thiết bị
    private void updateDenBuTheoTinhTrang() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Trường hợp 'Tốt' => Đền Bù = 0
            String updateTot = "UPDATE b0_kiemtrachitiet SET DenBu = 0 WHERE TinhTrang = 'Tốt'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateTot)) {
                int rows = pstmt.executeUpdate();
                System.out.println("✔ Đã cập nhật " + rows + " dòng có Tình Trạng = 'Tốt' (Đền Bù = 0)");
            }

            // Trường hợp 'Hỏng' => Lấy Đền Bù từ bảng a7_thietbi
            String updateHong = """
                UPDATE b0_kiemtrachitiet kt
                JOIN a7_thietbi tb ON kt.MaThietBi = tb.MaThietBi
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

    // Phương thức chính để gọi logic kiểm tra và cập nhật
    // public static void main(String[] args) {
    //     String authToken = "Bearer your-auth-token";  // Token lấy từ nơi phù hợp
    //     CheckDetail checkDetail = new CheckDetail(authToken);
        
    //     // Thực hiện kiểm tra và cập nhật đền bù
    //     ApiResponse response = checkDetail.processCheckDetail("b0_kiemtrachitiet", "TinhTrang", "Tot");
        
    //     if (response.isSuccess()) {
    //         System.out.println("✅ Đã xử lý xong!");
    //     } else {
    //         System.err.println("❌ Có lỗi xảy ra khi xử lý.");
    //     }
    // }
}
