package project.appPack.Backend.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> getTableInfo() {
        String query = "SELECT TABLE_NAME, TABLE_COMMENT " +
                       "FROM INFORMATION_SCHEMA.TABLES " +
                       "WHERE TABLE_SCHEMA = DATABASE() " +
                       "AND TABLE_TYPE = 'BASE TABLE'";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Map<String, String> table = new HashMap<>();
            String tableName = rs.getString("TABLE_NAME");
            String tableComment = rs.getString("TABLE_COMMENT");
            table.put("name", tableName);
            table.put("comment", tableComment != null && !tableComment.isEmpty() ? tableComment : tableName); // Dự phòng nếu comment rỗng
            return table;
        });
    }

    public List<String> getTableNames() {
        return jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> rs.getString(1));
    }

    public List<Map<String, Object>> getTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(query);
    }

    public void addRow(String tableName, Map<String, Object> data) {
        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", data.keySet().stream().map(k -> "?").toList());
        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        jdbcTemplate.update(query, data.values().toArray());
    }

    public void updateRow(String tableName, String idColumn, Object idValue, Map<String, Object> data) {
        String setClause = String.join(", ", data.keySet().stream().map(k -> k + " = ?").toList());
        String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumn + " = ?";
        List<Object> params = new ArrayList<>(data.values());
        params.add(idValue);
        jdbcTemplate.update(query, params.toArray());
    }

    public void deleteRow(String tableName, String idColumn, Object idValue) {
        String query = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
        jdbcTemplate.update(query, idValue);
    }
}