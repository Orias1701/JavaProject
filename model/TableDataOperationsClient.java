package model;

import controller.LogHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import model.ApiClient.ApiResponse;
public class TableDataOperationsClient {
    // Địa chỉ cơ bản của API
    private static final String BASE_URL = "http://localhost:8080/api";

    // Dùng để gửi HTTP request
    private final HttpUtil httpUtil = new HttpUtil();

    // Dùng để chuyển đổi giữa JSON và Map
    private final JsonParser jsonParser = new JsonParser();

    // Header chứa token xác thực
    private final String authHeader;

    // Hàm khởi tạo nhận vào chuỗi token xác thực
    public TableDataOperationsClient(String authHeader) {
        this.authHeader = authHeader;
    }

    // Hàm thêm dòng mới vào bảng
    public ApiResponse addRow(String tableName, Map<String, Object> data) {
        try {
            // Kiểm tra tên bảng hợp lệ
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }

            // Chuyển dữ liệu từ Map sang chuỗi JSON
            String jsonBody = jsonParser.mapToJson(data);

            // Gửi yêu cầu POST đến API
            LogHandler.logInfo("Gửi yêu cầu POST: tableName=" + tableName + ", body=" + jsonBody);
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName),
                "POST",
                authHeader,
                jsonBody
            );

            // Ghi log phản hồi
            LogHandler.logInfo("Add row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

            // Phân tích mã phản hồi và nội dung
            if (response.statusCode == 201) {
                return new ApiResponse(true, "Thêm thành công");
            } else if (response.body.contains("Duplicate entry")) {
                String value = response.body.contains("'") ? response.body.split("'")[1] : "không xác định";
                return new ApiResponse(false, "Mã " + value + " đã tồn tại trong bảng");
            } else if (response.body.contains("foreign key constraint fails")) {
                String relatedTable = response.body.contains("`") ? response.body.split("`")[3] : "khác";
                return new ApiResponse(false, "Dữ liệu không hợp lệ vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body);
            }
        } catch (Exception e) {
            // Ghi log lỗi nếu xảy ra lỗi mạng
            LogHandler.logError("Error adding row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    // Hàm cập nhật một dòng trong bảng
    public ApiResponse updateRow(String tableName, String keyColumn, Object keyValue, Map<String, Object> data) {
        try {
            // Kiểm tra khóa chính và tên bảng hợp lệ
            if (keyColumn == null || keyColumn.isEmpty()) {
                LogHandler.logError("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }

            // Ghi log thông tin gửi yêu cầu
            LogHandler.logInfo("Gửi yêu cầu PUT: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);
            String jsonBody = jsonParser.mapToJson(data);
            LogHandler.logInfo("JSON body: " + jsonBody);

            // Gửi yêu cầu PUT đến API
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName + "/" + keyValue),
                "PUT",
                authHeader,
                jsonBody
            );

            // Ghi log kết quả phản hồi
            LogHandler.logInfo("Update row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

            // Xử lý phản hồi
            if (response.statusCode == 200) {
                return new ApiResponse(true, "Cập nhật thành công");
            } else if (response.body.contains("Duplicate entry")) {
                String value = response.body.contains("'") ? response.body.split("'")[1] : "không xác định";
                return new ApiResponse(false, "Mã " + value + " đã tồn tại trong bảng");
            } else if (response.body.contains("foreign key constraint fails")) {
                String relatedTable = response.body.contains("`") ? response.body.split("`")[3] : "khác";
                return new ApiResponse(false, "Dữ liệu không hợp lệ vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body);
            }
        } catch (Exception e) {
            // Ghi log lỗi khi cập nhật thất bại
            LogHandler.logError("Error updating row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    // Hàm xóa một dòng trong bảng
    public ApiResponse deleteRow(String tableName, String keyColumn, Object keyValue) {
        try {
            // Kiểm tra khóa chính và tên bảng
            if (keyColumn == null || keyColumn.isEmpty()) {
                LogHandler.logError("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }

            // Ghi log yêu cầu xóa
            LogHandler.logInfo("Gửi yêu cầu DELETE: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);

            // Gửi yêu cầu DELETE đến API
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName + "/" + keyValue),
                "DELETE",
                authHeader,
                ""
            );

            // Ghi log phản hồi
            LogHandler.logInfo("Delete row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

            // Xử lý kết quả
            if (response.statusCode == 200) {
                return new ApiResponse(true, "Xóa thành công");
            } else if (response.body.contains("foreign key constraint fails")) {
                String relatedTable = response.body.contains("`") ? response.body.split("`")[3] : "khác";
                return new ApiResponse(false, "Không thể xóa vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body);
            }
        } catch (Exception e) {
            // Ghi log lỗi khi xóa thất bại
            LogHandler.logError("Error deleting row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }
    public ApiResponse getRow(String tableName, String keyColumn, Object keyValue) {
        try {
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            if (keyColumn == null || keyColumn.isEmpty()) {
                LogHandler.logError("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }

            // Giả sử API chỉ cần tableName và keyValue, không cần keyColumn trong URL
            URI uri = URI.create(BASE_URL + "/table/" + tableName + "/" + keyValue);
            LogHandler.logInfo("Gửi yêu cầu GET: " + uri);

            HttpUtil.HttpResponse response = httpUtil.sendRequest(uri, "GET", authHeader, "");

            LogHandler.logInfo("Get row response: " + response.statusCode);
            LogHandler.logInfo("Raw JSON body: " + response.body);

            if (response.statusCode == 200) {
                ApiClient.TableDataResult result = jsonParser.parseTableDataWithColumns(response.body);

                if (result.data != null && !result.data.isEmpty()) {
                    Map<String, String> row = result.data.get(0);
                    return new ApiResponse(true, "Lấy dòng dữ liệu thành công", new HashMap<>(row));
                } else {
                    return new ApiResponse(false, "Không tìm thấy dữ liệu");
                }
            } else {
                return new ApiResponse(false, response.body);
            }

        } catch (Exception e) {
            LogHandler.logError("Error getting row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }
}
