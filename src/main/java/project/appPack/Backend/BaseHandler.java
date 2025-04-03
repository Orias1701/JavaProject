package project.appPack.Backend;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {

    protected static final String ACCOUNTS_DB_URL = "jdbc:mysql://localhost:3306/accounts";
    protected static final String ORDERS_DB_URL = "jdbc:mysql://localhost:3306/orders";
    protected static final String DB_USERNAME = "root";
    protected static final String DB_PASSWORD = "170105";

    protected boolean authenticate(HttpExchange exchange) throws IOException {
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
            Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Server error", e);
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
            return false;
        }
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}