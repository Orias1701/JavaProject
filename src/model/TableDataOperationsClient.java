package model;

import controller.LogHandler;
import model.ApiClient.ApiResponse;
import java.net.URI;
import java.util.Map;

public class TableDataOperationsClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpUtil httpUtil = new HttpUtil();
    private final JsonParser jsonParser = new JsonParser();
    private final String authHeader;

    public TableDataOperationsClient(String authHeader) {
        this.authHeader = authHeader;
    }

    public ApiResponse addRow(String tableName, Map<String, Object> data) {
        try {
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            String jsonBody = jsonParser.mapToJson(data);
            LogHandler.logInfo("Gửi yêu cầu POST: tableName=" + tableName + ", body=" + jsonBody);
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName),
                "POST",
                authHeader,
                jsonBody
            );
            LogHandler.logInfo("Add row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

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
            LogHandler.logError("Error adding row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    public ApiResponse updateRow(String tableName, String keyColumn, Object keyValue, Map<String, Object> data) {
        try {
            if (keyColumn == null || keyColumn.isEmpty()) {
                LogHandler.logError("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            LogHandler.logInfo("Gửi yêu cầu PUT: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);
            String jsonBody = jsonParser.mapToJson(data);
            LogHandler.logInfo("JSON body: " + jsonBody);
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName + "/" + keyValue),
                "PUT",
                authHeader,
                jsonBody
            );
            LogHandler.logInfo("Update row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

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
            LogHandler.logError("Error updating row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    public ApiResponse deleteRow(String tableName, String keyColumn, Object keyValue) {
        try {
            if (keyColumn == null || keyColumn.isEmpty()) {
                LogHandler.logError("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                LogHandler.logError("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            LogHandler.logInfo("Gửi yêu cầu DELETE: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);
            HttpUtil.HttpResponse response = httpUtil.sendRequest(
                URI.create(BASE_URL + "/table/" + tableName + "/" + keyValue),
                "DELETE",
                authHeader,
                ""
            );
            LogHandler.logInfo("Delete row response: " + response.statusCode);
            LogHandler.logInfo("Body: " + response.body);

            if (response.statusCode == 200) {
                return new ApiResponse(true, "Xóa thành công");
            } else if (response.body.contains("foreign key constraint fails")) {
                String relatedTable = response.body.contains("`") ? response.body.split("`")[3] : "khác";
                return new ApiResponse(false, "Không thể xóa vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body);
            }
        } catch (Exception e) {
            LogHandler.logError("Error deleting row: " + e.getMessage(), e);
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }
}