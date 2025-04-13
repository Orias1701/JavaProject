package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class LoginHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        if (authenticate(exchange)) {
            sendResponse(exchange, 200, "Login successful");
        }
    }
}