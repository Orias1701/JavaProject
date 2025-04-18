package view.MainRegion;

import java.util.*;
import javax.swing.*;
import model.ApiClient;
import model.ApiClient.TableDataResult;

public class Enum {

    // Define the Status enum
    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING,
        COMPLETED
    }

    // Phương thức khởi tạo JComboBox từ enum Status
    public JComboBox<Status> createStatusComboBox() {
        // Tạo model JComboBox để lưu trữ các giá trị enum
        DefaultComboBoxModel<Status> model = new DefaultComboBoxModel<>();
        
        // Lấy danh sách bảng từ API
        Map<String, String> tableInfo = ApiClient.getTableInfo();
        
        if (tableInfo != null && !tableInfo.isEmpty()) {
            // Duyệt qua các bảng và cột, tìm kiếm kiểu ENUM
            for (String tableName : tableInfo.keySet()) {
                TableDataResult tableData = ApiClient.getTableData(tableName);
                
                // Nếu lấy được dữ liệu từ bảng, kiểm tra các cột kiểu ENUM
                if (tableData != null && !tableData.data.isEmpty()) {
                    for (Map<String, String> row : tableData.data) {
                        for (String columnName : row.keySet()) {
                            String value = row.get(columnName);
                            
                            // Kiểm tra nếu giá trị của cột là một ENUM hợp lệ
                            try {
                                Status status = Status.valueOf(value.toUpperCase());
                                model.addElement(status);  // Thêm enum vào model của JComboBox
                            } catch (IllegalArgumentException e) {
                                // Nếu không phải enum hợp lệ, bỏ qua
                                System.out.println("Invalid enum value: " + value);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("Failed to fetch table info: Unable to retrieve table information.");
        }

        // Tạo JComboBox và gán model
        return new JComboBox<>(model);  // Trả về JComboBox đã tạo
    }
}
