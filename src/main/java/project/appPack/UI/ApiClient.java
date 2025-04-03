package project.appPack.UI;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import project.appPack.Backend.BaseHandler;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/tables";
    private static final String LOGIN_URL = "http://localhost:8080/api/login";
    private static final Gson gson = new Gson();
    private static String authHeader = null;

    // Login
    public static boolean login(String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        authHeader = "Basic " + encodedCredentials;
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Login response status: " + response.statusCode());
            System.out.println("Login response body: " + response.body());
            return response.statusCode() == 200;
        } catch (java.net.http.HttpConnectTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Connection timeout error during login", e);
            return false;
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Timeout error during login", e);
            return false;
        } catch (java.io.IOException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "I/O error during login", e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Request interrupted during login", e);
            return false;
        }
    }

    public static Map<String, String> getTableInfo() {
        Map<String, String> tableInfo = new HashMap<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", authHeader)
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

    public static List<Map<String, String>> getTableData(String tableName) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL.replace("tables", "table/" + tableName))) // Thay BASE_URL thành http://localhost:8080/api/table/{tableName}
                .header("Authorization", authHeader)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Table data status: " + response.statusCode());
            System.out.println("Table data body: " + response.body());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Map<String, String>>>(){}.getType());
            } else {
                throw new IOException("API returned " + response.statusCode());
            }
        } catch (java.net.http.HttpConnectTimeoutException e) {
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Connection timeout error while fetching table data", e);
            return new ArrayList<>();
        } catch (java.net.http.HttpTimeoutException e) {
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Timeout error while fetching table data", e);
            return new ArrayList<>();
        } catch (java.io.IOException e) {
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "I/O error while fetching table data", e);
            return new ArrayList<>();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Request interrupted while fetching table data", e);
            return new ArrayList<>();
        }
    }

    public static void addRow(String tableName, Map<String, Object> data) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tableName))
                .header("Authorization", authHeader)
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
                .header("Authorization", authHeader)
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
                .header("Authorization", authHeader)
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