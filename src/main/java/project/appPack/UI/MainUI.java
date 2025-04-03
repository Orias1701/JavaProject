// package project.appPack.UI;

// import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.Dimension;
// import java.io.IOException;
// import java.io.OutputStream;
// import java.net.InetSocketAddress;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.Base64;

// import javax.swing.JFrame;
// import javax.swing.JOptionPane;
// import javax.swing.SwingUtilities;

// import com.sun.net.httpserver.HttpExchange;
// import com.sun.net.httpserver.HttpHandler;
// import com.sun.net.httpserver.HttpServer;

// import project.appPack.UI.MainRegion.MainPanel;
// import project.appPack.UI.MenuRegion.MenuPanel;
// import project.appPack.UI.MenuRegion.MenuScrollPane;

// public class MainUI extends JFrame {

//     private static final String ACCOUNTS_DB_URL = "jdbc:mysql://localhost:3306/accounts";
//     private static final String ORDERS_DB_URL = "jdbc:mysql://localhost:3306/orders";
//     private static final String DB_USERNAME = "root";
//     private static final String DB_PASSWORD = "170105";

//     public MainUI() {
//         setTitle("Order Management System");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(1280, 720);
//         setMinimumSize(new Dimension(900, 700));
//         setLayout(new BorderLayout());

//         getContentPane().setBackground(Color.decode("#FFFFFF"));

//         startServer();

//         add(new LoginPanel(this));

//         setVisible(true);
//     }

//     private void startServer() {
//         new Thread(() -> {
//             try {
//                 HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//                 server.createContext("/api/login", new LoginHandler());
//                 server.createContext("/api/tables", new TablesHandler());
//                 server.createContext("/api/table/", new TableDataHandler()); // Endpoint mới
//                 server.setExecutor(null);
//                 server.start();
//                 System.out.println("Server started on port 8080");
//             } catch (IOException e) {
//                 e.printStackTrace();
//                 JOptionPane.showMessageDialog(this, "Không thể khởi động server: " + e.getMessage(),
//                         "Lỗi", JOptionPane.ERROR_MESSAGE);
//             }
//         }).start();
//     }
    
//     // Handler mới để lấy nội dung bảng
//     private class TableDataHandler implements HttpHandler {
//         @Override
//         public void handle(HttpExchange exchange) throws IOException {
//             if (!"GET".equals(exchange.getRequestMethod())) {
//                 sendResponse(exchange, 405, "Method Not Allowed");
//                 return;
//             }
    
//             String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
//             if (authHeader == null || !authHeader.startsWith("Basic ")) {
//                 sendResponse(exchange, 401, "Unauthorized");
//                 return;
//             }
    
//             String base64Credentials = authHeader.substring("Basic ".length());
//             String credentials = new String(Base64.getDecoder().decode(base64Credentials));
//             String[] parts = credentials.split(":");
//             String username = parts[0];
//             String password = parts.length > 1 ? parts[1] : "";
    
//             try (Connection conn = DriverManager.getConnection(ACCOUNTS_DB_URL, DB_USERNAME, DB_PASSWORD);
//                  PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
//                 stmt.setString(1, username);
//                 ResultSet rs = stmt.executeQuery();
//                 if (!rs.next() || !rs.getString("password").equals(password)) {
//                     sendResponse(exchange, 401, "Unauthorized");
//                     return;
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 sendResponse(exchange, 500, "Server error: " + e.getMessage());
//                 return;
//             }
    
//             // Lấy tên bảng từ URL (ví dụ: /api/table/table1)
//             String path = exchange.getRequestURI().getPath();
//             String tableName = path.substring("/api/table/".length());
//             if (tableName.isEmpty()) {
//                 sendResponse(exchange, 400, "Bad Request: Table name missing");
//                 return;
//             }
    
//             try (Connection conn = DriverManager.getConnection(ORDERS_DB_URL, DB_USERNAME, DB_PASSWORD);
//                  PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName)) {
//                 ResultSet rs = stmt.executeQuery();
//                 StringBuilder json = new StringBuilder("[");
//                 boolean firstRow = true;
//                 int columnCount = rs.getMetaData().getColumnCount();
//                 while (rs.next()) {
//                     if (!firstRow) json.append(",");
//                     json.append("{");
//                     for (int i = 1; i <= columnCount; i++) {
//                         String columnName = rs.getMetaData().getColumnName(i);
//                         String value = rs.getString(i);
//                         if (i > 1) json.append(",");
//                         json.append("\"").append(columnName).append("\":\"").append(value != null ? value : "").append("\"");
//                     }
//                     json.append("}");
//                     firstRow = false;
//                 }
//                 json.append("]");
//                 sendResponse(exchange, 200, json.toString());
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 sendResponse(exchange, 500, "Server error: " + e.getMessage());
//             }
//         }
//     }

//     public void showMainInterface() {
//         getContentPane().removeAll();
//         setLayout(new BorderLayout());

//         MainPanel mainPanel = new MainPanel();
//         add(mainPanel, BorderLayout.CENTER);

//         MenuPanel menuPanel = new MenuPanel(mainPanel);
//         MenuScrollPane scrollPanel = new MenuScrollPane(menuPanel);
//         scrollPanel.setPreferredSize(new Dimension(160, 720));
//         add(scrollPanel, BorderLayout.WEST);

//         revalidate();
//         repaint();
//     }

//     private class LoginHandler implements HttpHandler {
//         @Override
//         public void handle(HttpExchange exchange) throws IOException {
//             if (!"POST".equals(exchange.getRequestMethod())) {
//                 sendResponse(exchange, 405, "Method Not Allowed");
//                 return;
//             }

//             String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
//             if (authHeader == null || !authHeader.startsWith("Basic ")) {
//                 sendResponse(exchange, 401, "Unauthorized");
//                 return;
//             }

//             String base64Credentials = authHeader.substring("Basic ".length());
//             String credentials = new String(Base64.getDecoder().decode(base64Credentials));
//             String[] parts = credentials.split(":");
//             String username = parts[0];
//             String password = parts.length > 1 ? parts[1] : "";

//             try (Connection conn = DriverManager.getConnection(ACCOUNTS_DB_URL, DB_USERNAME, DB_PASSWORD);
//                  PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
//                 stmt.setString(1, username);
//                 ResultSet rs = stmt.executeQuery();
//                 if (rs.next()) {
//                     String storedPassword = rs.getString("password");
//                     if (storedPassword.equals(password)) {
//                         sendResponse(exchange, 200, "Login successful");
//                     } else {
//                         sendResponse(exchange, 401, "Unauthorized");
//                     }
//                 } else {
//                     sendResponse(exchange, 401, "Unauthorized");
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 sendResponse(exchange, 500, "Server error: " + e.getMessage());
//             }
//         }
//     }

//     private class TablesHandler implements HttpHandler {
//         @Override
//         public void handle(HttpExchange exchange) throws IOException {
//             if (!"GET".equals(exchange.getRequestMethod())) {
//                 sendResponse(exchange, 405, "Method Not Allowed");
//                 return;
//             }
    
//             String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
//             if (authHeader == null || !authHeader.startsWith("Basic ")) {
//                 sendResponse(exchange, 401, "Unauthorized");
//                 return;
//             }
    
//             String base64Credentials = authHeader.substring("Basic ".length());
//             String credentials = new String(Base64.getDecoder().decode(base64Credentials));
//             String[] parts = credentials.split(":");
//             String username = parts[0];
//             String password = parts.length > 1 ? parts[1] : "";
    
//             try (Connection conn = DriverManager.getConnection(ACCOUNTS_DB_URL, DB_USERNAME, DB_PASSWORD);
//                  PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
//                 stmt.setString(1, username);
//                 ResultSet rs = stmt.executeQuery();
//                 if (!rs.next() || !rs.getString("password").equals(password)) {
//                     sendResponse(exchange, 401, "Unauthorized");
//                     return;
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 sendResponse(exchange, 500, "Server error: " + e.getMessage());
//                 return;
//             }
    
//             try (Connection conn = DriverManager.getConnection(ORDERS_DB_URL, DB_USERNAME, DB_PASSWORD);
//                  PreparedStatement stmt = conn.prepareStatement(
//                      "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES " +
//                      "WHERE TABLE_SCHEMA = 'orders' AND TABLE_TYPE = 'BASE TABLE'")) {
//                 ResultSet rs = stmt.executeQuery();
//                 StringBuilder json = new StringBuilder("[");
//                 boolean first = true;
//                 while (rs.next()) {
//                     if (!first) json.append(",");
//                     String name = rs.getString("TABLE_NAME");
//                     String comment = rs.getString("TABLE_COMMENT");
//                     if (comment == null || comment.isEmpty()) comment = name;
//                     json.append("{\"name\":\"").append(name).append("\",\"comment\":\"").append(comment).append("\"}");
//                     first = false;
//                 }
//                 json.append("]");
//                 sendResponse(exchange, 200, json.toString());
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 sendResponse(exchange, 500, "Server error: " + e.getMessage());
//             }
//         }
//     }

//     private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
//         byte[] responseBytes = response.getBytes("UTF-8");
//         exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
//         exchange.sendResponseHeaders(statusCode, responseBytes.length);
//         OutputStream os = exchange.getResponseBody();
//         os.write(responseBytes);
//         os.flush();
//         os.close();
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(MainUI::new);
//     }
// }

package project.appPack.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import project.appPack.Backend.SimpleServer;
import project.appPack.UI.MainRegion.MainPanel;
import project.appPack.UI.MenuRegion.MenuPanel;
import project.appPack.UI.MenuRegion.MenuScrollPane;

public class MainUI extends JFrame {

    public MainUI() {
        setTitle("Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#FFFFFF"));

        SimpleServer.start();

        add(new LoginPanel(this));

        setVisible(true);
    }

    public void showMainInterface() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        MainPanel mainPanel = new MainPanel();
        add(mainPanel, BorderLayout.CENTER);

        MenuPanel menuPanel = new MenuPanel(mainPanel);
        MenuScrollPane scrollPanel = new MenuScrollPane(menuPanel);
        scrollPanel.setPreferredSize(new Dimension(160, 720));
        add(scrollPanel, BorderLayout.WEST);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}