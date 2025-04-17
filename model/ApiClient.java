package model;

import controller.LogHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    // Địa chỉ gốc của API backend
    private static final String BASE_URL = "http://localhost:8080/api";

    // Chuỗi header dùng để xác thực (Basic Auth)
    private static String authHeader;

    // Tiện ích HTTP và JSON
    private static final HttpUtil httpUtil = new HttpUtil();
    private static final JsonParser jsonParser = new JsonParser();

    // Lớp chứa kết quả trả về từ API sau khi lấy dữ liệu bảng
    public static class TableDataResult {
        public final List<Map<String, String>> data;             // Dữ liệu bảng (mỗi dòng là 1 map key-value)
        public final Map<String, String> columnComments;         // Comment cho từng cột (dùng để hiển thị tên rõ ràng)
        public final String keyColumn;                           // Tên cột khóa chính của bảng

        public TableDataResult(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn) {
            this.data = data;
            this.columnComments = columnComments;
            this.keyColumn = keyColumn;
            LogHandler.logInfo("Khóa chính API: " + keyColumn);
        }
    }

    // Lớp chứa phản hồi đơn giản từ API
    public static class ApiResponse {
        private final boolean success;
        public final String message;
        private final Map<String, Object> data;
    
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.data = null;
        }
    
        public ApiResponse(boolean success, String message, Map<String, Object> data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        public boolean isSuccess() {
            return success;
        }
        public String getMessage() {
            return message;
        }
        public Map<String, Object> getData() {
            return data;
        }
    }

    /**
     * Gửi yêu cầu đăng nhập đến API và thiết lập header xác thực nếu thành công.
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return true nếu đăng nhập thành công, ngược lại false
     */
    public static boolean login(String username, String password) {
        // Mã hóa thông tin đăng nhập thành Basic Auth
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        authHeader = "Basic " + encodedCredentials;

        try {
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/login"),
                "POST",
                authHeader,
                ""
            );
            LogHandler.logInfo("Login response status: " + response.statusCode);
            LogHandler.logInfo("Login response body: " + response.body);
            return response.statusCode == 200;
        } catch (Exception e) {
            LogHandler.logError("Login error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy danh sách các bảng từ API và trả về Map chứa tên bảng và comment tương ứng.
     * @return Map<String, String> (tên bảng -> comment) hoặc thông báo lỗi
     */
    public static Map<String, String> getTableInfo() {
        try {
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/tables"),
                "GET",
                authHeader,
                ""
            );
            LogHandler.logInfo("Table info status: " + response.statusCode);
            LogHandler.logInfo("Table info body: " + response.body);
            if (response.statusCode == 200) {
                return jsonParser.parseTableInfo(response.body); // Parse JSON trả về thành Map
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "API returned " + response.statusCode);
                return error;
            }
        } catch (Exception e) {
            LogHandler.logError("Error occurred: " + e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch table info: " + e.getMessage());
            return error;
        }
    }

    /**
     * Lấy dữ liệu của bảng cụ thể từ API (bao gồm cả dữ liệu và thông tin các cột).
     * @param tableName tên bảng cần lấy
     * @return TableDataResult chứa danh sách dữ liệu, comment cột, và tên cột khóa chính
     */
    public static TableDataResult getTableData(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            LogHandler.logError("Error: tableName is null or empty");
            return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
        }
        try {
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName),
                "GET",
                authHeader,
                ""
            );
            LogHandler.logInfo("Table data status: " + response.statusCode);
            LogHandler.logInfo("Table data body: " + response.body);
            if (response.statusCode == 200) {
                return jsonParser.parseTableDataWithColumns(response.body); // Phân tích dữ liệu JSON
            } else {
                LogHandler.logError("API error for table " + tableName + ": Status " + response.statusCode);
                return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
            }
        } catch (Exception e) {
            LogHandler.logError("Error fetching table data for " + tableName + ": " + e.getMessage(), e);
            return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
        }
    }
}
