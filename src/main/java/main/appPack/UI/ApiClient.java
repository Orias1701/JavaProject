package main.appPack.UI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiClient {

    private static final String API_URL = "http://localhost:8080/api/tables";

    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                tableNames = gson.fromJson(response.body(), new TypeToken<List<String>>(){}.getType());
            } else {
                tableNames.add("Error: API returned " + response.statusCode());
            }
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            Logger.getLogger(ApiClient.class.getName()).log(Level.SEVERE, "Error connecting to API", e);
            tableNames.add("Error connecting to API");
        }
        return tableNames;
    }
}