package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Properties;

public abstract class BaseHandler implements HttpHandler, HttpHandlerLogic {
    protected static final String ACCOUNTS_DB_URL;
    protected static final String DATA_DB_URL;
    protected static final String DB_USERNAME;
    protected static final String DB_PASSWORD;

    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            ACCOUNTS_DB_URL = props.getProperty("accounts.db.url");
            DATA_DB_URL = props.getProperty("data.db.url");
            DB_USERNAME = props.getProperty("db.username");
            DB_PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticate(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendResponse(exchange, 401, "Unauthorized");
            return false;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts.length > 1 ? parts[1] : "";

        try (Connection conn = DriverManager.getConnection(ACCOUNTS_DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString("password").equals(password)) {
                return true;
            } else {
                sendResponse(exchange, 401, "Unauthorized");
                return false;
            }
        } catch (Exception e) {
            LogHandler.logError("Authentication error: " + e.getMessage(), e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}