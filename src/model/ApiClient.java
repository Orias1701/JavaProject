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

        public TableDataResult(List<Map<String, String>> data, Map<String, String> columnComments) {
            this.data = data;
            this.columnComments = columnComments;
        }
    }

    public static TableDataResult getTableData(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            System.err.println("Error: tableName is null or empty");
            return new TableDataResult(new ArrayList<>(), new HashMap<>());
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
                return new TableDataResult(new ArrayList<>(), new HashMap<>());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching table data for " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return new TableDataResult(new ArrayList<>(), new HashMap<>());
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

    /**
     * Parses the JSON response from the API to extract table data and column comments.
     *
     * @param json The JSON response string.
     * @return A TableDataResult object containing the parsed data and column comments.
     */
    private static TableDataResult parseTableDataWithColumns(String json) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> columnComments = new HashMap<>();

        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                return new TableDataResult(data, columnComments);
            }
            json = json.substring(1, json.length() - 1);

            String[] parts = json.split(",\"data\":");
            if (parts.length != 2) {
                return new TableDataResult(data, columnComments);
            }

            String columnsJson = parts[0].replace("\"columns\":", "").trim();
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

            String dataJson = parts[1].substring(1, parts[1].length() - 1);
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

            return new TableDataResult(data, columnComments);
        } catch (Exception e) {
            System.err.println("Error parsing table data: " + e.getMessage());
            e.printStackTrace();
            return new TableDataResult(data, columnComments);
        }
    }

    // private static List<Map<String, String>> parseTableData(String json) {
    //     List<Map<String, String>> tableData = new ArrayList<>();
    //     json = json.substring(1, json.length() - 1);
    //     if (json.isEmpty()) return tableData;

    //     String[] rows = json.split("},");
    //     for (String row : rows) {
    //         row = row.replace("{", "").replace("}", "").trim();
    //         String[] keyValuePairs = row.split(",");
    //         Map<String, String> rowData = new HashMap<>();
    //         for (String pair : keyValuePairs) {
    //             String[] parts = pair.split(":");
    //             String key = parts[0].replace("\"", "").trim();
    //             String value = parts[1].replace("\"", "").trim();
    //             rowData.put(key, value);
    //         }
    //         tableData.add(rowData);
    //     }
    //     return tableData;
    // }

    public static void addRow(String tableName, Map<String, Object> data) {
        System.out.println("Adding row to table: " + tableName);
    }

    public static void updateRow(String tableName, String columnName, Object idValue, Map<String, Object> data) {
        System.out.println("Updating row in table: " + tableName);
    }

    public static void deleteRow(String tableName, String columnName, Object idValue) {
        System.out.println("Deleting row from table: " + tableName);
    }
}