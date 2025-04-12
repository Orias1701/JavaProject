package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class HttpHandler {
    public static class Response {
        private final int statusCode;
        private final String body;
        private final Exception error;

        public Response(int statusCode, String body, Exception error) {
            this.statusCode = statusCode;
            this.body = body != null ? body : "";
            this.error = error;
        }

        public boolean success() {
            return error == null;
        }

        public int statusCode() {
            return statusCode;
        }

        public String body() {
            return body;
        }

        public String errorMessage() {
            return error != null ? error.getMessage() : "";
        }
    }

    private final HttpClient client;

    public HttpHandler() {
        this.client = HttpClient.newHttpClient();
    }

    public String encodeCredentials(String credentials) {
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    public Response sendGet(String url, String authHeader) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();
        return sendRequest(request);
    }

    public Response sendPost(String url, String authHeader, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(body.isEmpty() ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build();
        return sendRequest(request);
    }

    public Response sendPut(String url, String authHeader, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return sendRequest(request);
    }

    public Response sendDelete(String url, String authHeader) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .DELETE()
                .build();
        return sendRequest(request);
    }

    private Response sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Response(response.statusCode(), response.body(), null);
        } catch (IOException | InterruptedException e) {
            return new Response(0, "", e);
        }
    }
}