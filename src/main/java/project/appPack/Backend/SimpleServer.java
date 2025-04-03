package project.appPack.Backend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

public class SimpleServer {

    public static void start() {
        new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
                server.createContext("/api/login", new LoginHandler());
                server.createContext("/api/tables", new TablesHandler());
                server.createContext("/api/table/", new TableDataHandler());
                server.setExecutor(null);
                server.start();
                System.out.println("Server started on port 8080");
            } catch (IOException e) {
                Logger.getLogger(BaseHandler.class.getName()).log(Level.SEVERE, "Server error", e);

            }
        }).start();
    }
}