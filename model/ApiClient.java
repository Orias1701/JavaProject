package model;

import controller.LogHandler;
import controller.UserSession;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static String authHeader;
    private static final HttpUtil httpUtil = new HttpUtil();
    private static final JsonParser jsonParser = new JsonParser();

    public static class TableDataResult {
        public final List<Map<String, String>> data;
        public final Map<String, String> columnComments;
        public final String keyColumn;
        public Map<String, String> columnTypes;

        public TableDataResult(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn) {
            this.data = data;
            this.columnComments = columnComments;
            this.keyColumn = keyColumn;
            LogHandler.logInfo("Khóa chính API: " + keyColumn);
        }
    }

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

    public static boolean login(String username, String password) {
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
            if (response.statusCode == 200) {
                // Phân tích JSON để lấy thông tin quyền
                Map<String, Object> responseData = jsonParser.parseLoginResponse(response.body);
                if (responseData.containsKey("permissions")) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> permissions = (Map<String, String>) responseData.get("permissions");
                    UserSession.setUser(
                        (String) responseData.get("username"),
                        (String) responseData.get("group"),
                        permissions
                    );
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            LogHandler.logError("Login error: " + e.getMessage(), e);
            return false;
        }
    }

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
                return jsonParser.parseTableInfo(response.body);
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
                return jsonParser.parseTableDataWithColumns(response.body);
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