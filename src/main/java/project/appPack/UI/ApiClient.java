package project.appPack.UI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/tables";
    private static final Gson gson = new Gson();

    public static Map<String, String> getTableInfo() {
        Map<String, String> tableInfo = new HashMap<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<Map<String, String>> tableList = gson.fromJson(response.body(), 
                    new TypeToken<List<Map<String, String>>>(){}.getType());
                for (Map<String, String> table : tableList) {
                    String tableName = table.get("name");
                    String tableComment = table.get("comment");
                    tableInfo.put(tableName, tableComment);
                }
            } else {
                tableInfo.put("error", "Error: API returned " + response.statusCode());
            }
        } catch (java.net.http.HttpConnectTimeoutException e) {
            tableInfo.put("error", "Connection timeout error while connecting to API");
        } catch (java.net.http.HttpTimeoutException e) {
            tableInfo.put("error", "Timeout error while connecting to API");
        } catch (java.io.IOException e) {
            tableInfo.put("error", "I/O error while connecting to API");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            tableInfo.put("error", "Request interrupted while connecting to API");
        }
        return tableInfo;
    }

    public static List<String> getTableNames() {
        return new ArrayList<>(getTableInfo().keySet());
    }

    public static List<Map<String, Object>> getTableData(String tableName) {
        List<Map<String, Object>> tableData = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tableName))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                tableData = gson.fromJson(response.body(), new TypeToken<List<Map<String, Object>>>(){}.getType());
            }
        } catch (java.net.http.HttpConnectTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Connection timeout error while fetching table data", e);
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Timeout error while fetching table data", e);
        } catch (java.io.IOException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "I/O error while fetching table data", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Request interrupted while fetching table data", e);
        }
        return tableData;
    }

    public static void addRow(String tableName, Map<String, Object> data) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tableName))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpConnectTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Connection timeout error occurred while updating row", e);
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Timeout error occurred while updating row", e);
        } catch (java.io.IOException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "I/O error occurred while updating row", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Request interrupted while updating row", e);
        }
    }

    public static void updateRow(String tableName, String idColumn, Object idValue, Map<String, Object> data) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tableName + "/" + idColumn + "/" + idValue))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpConnectTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Connection timeout error occurred while updating row", e);
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Timeout error occurred while updating row", e);
        } catch (java.io.IOException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "I/O error occurred while updating row", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Request interrupted while updating row", e);
        }
    }

    public static void deleteRow(String tableName, String idColumn, Object idValue) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tableName + "/" + idColumn + "/" + idValue))
                .DELETE()
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Timeout error occurred while deleting row", e);
        } catch (java.io.IOException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "I/O error occurred while deleting row", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Request interrupted while deleting row", e);
        }
    }
}