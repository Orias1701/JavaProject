package main.appPack.UI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/customer";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "170105";

    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            while (rs.next()) {
                tableNames.add(rs.getString(1));
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, "Error loading tables", e);
            tableNames.add("Error loading tables");
        }
        return tableNames;
    }
}