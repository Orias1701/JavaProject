package project.appPack.UI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/tables";
    private static final Gson gson = new Gson();

    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                tableNames = gson.fromJson(response.body(), new TypeToken<List<String>>(){}.getType());
            } else {
                tableNames.add("Error: API returned " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            tableNames.add("Error connecting to API");
        }
        return tableNames;
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
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}