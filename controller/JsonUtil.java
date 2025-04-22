package controller;

import java.sql.ResultSet;
// import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    public static Map<String, String> parseJson(String json) {
        // Existing parseJson implementation
        Map<String, String> result = new LinkedHashMap<>();
        // ... (implement as needed)
        return result;
    }

    public static String buildColumnsJsonFromList(List<Map<String, String>> columns) {
        // Existing buildColumnsJsonFromList implementation
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < columns.size(); i++) {
            Map<String, String> column = columns.get(i);
            json.append("{");
            json.append("\"name\":\"").append(column.get("name")).append("\",");
            json.append("\"comment\":\"").append(column.get("comment")).append("\",");
            json.append("\"dataType\":\"").append(column.get("dataType")).append("\"");
            json.append("}");
            if (i < columns.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    public static String buildDataJson(ResultSet rs) throws Exception {
        // Existing buildDataJson implementation
        StringBuilder json = new StringBuilder("[");
        boolean firstRow = true;
        while (rs.next()) {
            if (!firstRow) {
                json.append(",");
            }
            json.append("{");
            boolean firstColumn = true;
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if (!firstColumn) {
                    json.append(",");
                }
                String columnName = rs.getMetaData().getColumnName(i);
                String value = rs.getString(i) != null ? rs.getString(i) : "";
                json.append("\"").append(columnName).append("\":\"").append(value).append("\"");
                firstColumn = false;
            }
            json.append("}");
            firstRow = false;
        }
        json.append("]");
        return json.toString();
    }

    public static String buildPrimaryKeysJson(List<String> primaryKeys) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < primaryKeys.size(); i++) {
            json.append("\"").append(primaryKeys.get(i)).append("\"");
            if (i < primaryKeys.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}