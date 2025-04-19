package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;

public class LoginHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        if (authenticate(exchange)) {
            // Trả về thông tin người dùng và quyền truy cập
            String username = UserSession.getCurrentUsername();
            String group = UserSession.getCurrentGroup();
            Map<String, String> permissions = UserSession.getTablePermissions();
            String jsonResponse = String.format(
                "{\"message\":\"Login successful\",\"username\":\"%s\",\"group\":\"%s\",\"permissions\":%s}",
                username, group, mapToJson(permissions)
            );
            sendResponse(exchange, 200, jsonResponse);
        }
    }

    private String mapToJson(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            if (i < map.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("}");
        return json.toString();
    }
}