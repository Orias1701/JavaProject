package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/tables";
    private static String authHeader;

    public static class ApiResponse {
        public final boolean success;
        public final String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static boolean login(String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        authHeader = "Basic " + encodedCredentials;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status: " + response.statusCode());
            System.out.println("Login response body: " + response.body());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, String> getTableInfo() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", authHeader)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Table info status: " + response.statusCode());
            System.out.println("Table info body: " + response.body());
            if (response.statusCode() == 200) {
                return parseTableInfo(response.body());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "API returned " + response.statusCode());
                return error;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error occurred: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch table info: " + e.getMessage());
            return error;
        }
    }

    public static class TableDataResult {
        public final List<Map<String, String>> data;
        public final Map<String, String> columnComments;
        public final String keyColumn;

        public TableDataResult(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn) {
            this.data = data;
            this.columnComments = columnComments;
            this.keyColumn = keyColumn;
            System.out.println("Khóa chính API: " + keyColumn);
        }
    }

    public static TableDataResult getTableData(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            System.err.println("Error: tableName is null or empty");
            return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL.replace("tables", "table/" + tableName)))
                .header("Authorization", authHeader)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Table data status: " + response.statusCode());
            System.out.println("Table data body: " + response.body());
            if (response.statusCode() == 200) {
                return parseTableDataWithColumns(response.body());
            } else {
                System.err.println("API error for table " + tableName + ": Status " + response.statusCode());
                return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching table data for " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return new TableDataResult(new ArrayList<>(), new HashMap<>(), "");
        }
    }

    private static Map<String, String> parseTableInfo(String json) {
        Map<String, String> tableInfo = new HashMap<>();
        json = json.substring(1, json.length() - 1);
        if (json.isEmpty()) return tableInfo;

        String[] entries = json.split("},");
        for (String entry : entries) {
            entry = entry.replace("{", "").replace("}", "").trim();
            String[] keyValuePairs = entry.split(",");
            String name = "";
            String comment = "";
            for (String pair : keyValuePairs) {
                String[] parts = pair.split(":");
                String key = parts[0].replace("\"", "").trim();
                String value = parts[1].replace("\"", "").trim();
                if ("name".equals(key)) name = value;
                if ("comment".equals(key)) comment = value;
            }
            tableInfo.put(name, comment);
        }
        return tableInfo;
    }

    private static TableDataResult parseTableDataWithColumns(String json) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> columnComments = new HashMap<>();
        String keyColumn = "";

        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                System.err.println("Invalid JSON format");
                return new TableDataResult(data, columnComments, keyColumn);
            }
            json = json.substring(1, json.length() - 1);

            String[] parts = json.split(",\"columns\":");
            if (parts.length != 2) {
                System.err.println("Invalid JSON structure");
                return new TableDataResult(data, columnComments, keyColumn);
            }

            String[] headerParts = parts[0].split(",\"data\":");
            String keyColumnJson = headerParts[0].replace("\"keyColumn\":", "").trim();
            if (keyColumnJson.startsWith("\"") && keyColumnJson.endsWith("\"")) {
                keyColumn = keyColumnJson.substring(1, keyColumnJson.length() - 1);
            }

            String columnsJson = parts[1].substring(0, parts[1].indexOf(",\"data\":"));
            columnsJson = columnsJson.trim();
            if (columnsJson.startsWith("[") && columnsJson.endsWith("]")) {
                columnsJson = columnsJson.substring(1, columnsJson.length() - 1);
                if (!columnsJson.isEmpty()) {
                    String[] columnEntries = columnsJson.split("},");
                    for (String entry : columnEntries) {
                        entry = entry.replace("{", "").replace("}", "").trim();
                        String[] keyValuePairs = entry.split(",");
                        String name = "";
                        String comment = "";
                        for (String pair : keyValuePairs) {
                            String[] kv = pair.split(":");
                            String key = kv[0].replace("\"", "").trim();
                            String value = kv[1].replace("\"", "").trim();
                            if ("name".equals(key)) name = value;
                            if ("comment".equals(key)) comment = value;
                        }
                        columnComments.put(name, comment);
                    }
                }
            }

            String dataJson = parts[1].substring(parts[1].indexOf(",\"data\":") + 8);
            dataJson = dataJson.substring(1, dataJson.length() - 1);
            if (!dataJson.isEmpty()) {
                String[] rows = dataJson.split("},");
                for (String row : rows) {
                    row = row.replace("{", "").replace("}", "").trim();
                    String[] keyValuePairs = row.split(",");
                    Map<String, String> rowData = new HashMap<>();
                    for (String pair : keyValuePairs) {
                        String[] kv = pair.split(":");
                        String key = kv[0].replace("\"", "").trim();
                        String value = kv[1].replace("\"", "").trim();
                        rowData.put(key, value);
                    }
                    data.add(rowData);
                }
            }

            return new TableDataResult(data, columnComments, keyColumn);
        } catch (Exception e) {
            System.err.println("Error parsing table data: " + e.getMessage());
            e.printStackTrace();
            return new TableDataResult(data, columnComments, keyColumn);
        }
    }

    private static String mapToJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(value).append("\"");
            }
            if (i < data.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("}");
        return json.toString();
    }

    public static ApiResponse addRow(String tableName, Map<String, Object> data) {
        try {
            if (tableName == null || tableName.isEmpty()) {
                System.err.println("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = mapToJson(data);
            System.out.println("Gửi yêu cầu POST: tableName=" + tableName + ", body=" + jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/table/" + tableName))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Add row response: " + response.statusCode());
            System.out.println("Body: " + response.body());

            if (response.statusCode() == 201) {
                return new ApiResponse(true, "Thêm thành công");
            } else if (response.body().contains("Duplicate entry")) {
                String value = response.body().contains("'") ? response.body().split("'")[1] : "không xác định";
                return new ApiResponse(false, "Mã " + value + " đã tồn tại trong bảng");
            } else if (response.body().contains("foreign key constraint fails")) {
                String relatedTable = response.body().contains("`") ? response.body().split("`")[3] : "khác";
                return new ApiResponse(false, "Dữ liệu không hợp lệ vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error adding row: " + e.getMessage());
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    public static ApiResponse updateRow(String tableName, String keyColumn, Object keyValue, Map<String, Object> data) {
        try {
            if (keyColumn == null || keyColumn.isEmpty()) {
                System.err.println("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                System.err.println("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            System.out.println("Gửi yêu cầu PUT: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = mapToJson(data);
            System.out.println("JSON body: " + jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/table/" + tableName + "/" + keyValue))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Update row response: " + response.statusCode());
            System.out.println("Body: " + response.body());

            if (response.statusCode() == 200) {
                return new ApiResponse(true, "Cập nhật thành công");
            } else if (response.body().contains("Duplicate entry")) {
                String value = response.body().contains("'") ? response.body().split("'")[1] : "không xác định";
                return new ApiResponse(false, "Mã " + value + " đã tồn tại trong bảng");
            } else if (response.body().contains("foreign key constraint fails")) {
                String relatedTable = response.body().contains("`") ? response.body().split("`")[3] : "khác";
                return new ApiResponse(false, "Dữ liệu không hợp lệ vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error updating row: " + e.getMessage());
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }

    public static ApiResponse deleteRow(String tableName, String keyColumn, Object keyValue) {
        try {
            if (keyColumn == null || keyColumn.isEmpty()) {
                System.err.println("Error: keyColumn is null or empty");
                return new ApiResponse(false, "Khóa chính không hợp lệ");
            }
            if (tableName == null || tableName.isEmpty()) {
                System.err.println("Error: tableName is null or empty");
                return new ApiResponse(false, "Tên bảng không hợp lệ");
            }
            System.out.println("Gửi yêu cầu DELETE: tableName=" + tableName + ", keyColumn=" + keyColumn + ", keyValue=" + keyValue);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/table/" + tableName + "/" + keyValue))
                    .header("Authorization", authHeader)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Delete row response: " + response.statusCode());
            System.out.println("Body: " + response.body());

            if (response.statusCode() == 200) {
                return new ApiResponse(true, "Xóa thành công");
            } else if (response.body().contains("foreign key constraint fails")) {
                String relatedTable = response.body().contains("`") ? response.body().split("`")[3] : "khác";
                return new ApiResponse(false, "Không thể xóa vì liên quan đến bảng " + relatedTable);
            } else {
                return new ApiResponse(false, response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error deleting row: " + e.getMessage());
            return new ApiResponse(false, "Lỗi mạng: " + e.getMessage());
        }
    }
}