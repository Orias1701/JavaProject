package controller;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    public static Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                return result;
            }
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length != 2) continue;
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                result.put(key, value);
            }
        } catch (Exception e) {
            LogHandler.logError("Lỗi phân tích JSON: " + json, e);
        }
        return result;
    }

    public static String buildColumnsJson(Map<String, String> columnMetadata) {
        StringBuilder columnsJson = new StringBuilder("[");
        boolean firstColumn = true;
        for (Map.Entry<String, String> entry : columnMetadata.entrySet()) {
            if (!firstColumn) columnsJson.append(",");
            columnsJson.append("{\"name\":\"").append(entry.getKey())
                       .append("\",\"comment\":\"").append(entry.getValue().replace("\"", "\\\""))
                       .append("\"}");
            firstColumn = false;
        }
        columnsJson.append("]");
        return columnsJson.toString();
    }

    public static String buildDataJson(ResultSet rs) throws Exception {
        StringBuilder dataJson = new StringBuilder("[");
        boolean firstRow = true;
        int columnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            if (!firstRow) dataJson.append(",");
            dataJson.append("{");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                String value = rs.getString(i);
                if (i > 1) dataJson.append(",");
                dataJson.append("\"").append(columnName).append("\":\"")
                       .append(value != null ? value.replace("\"", "\\\"") : "").append("\"");
            }
            dataJson.append("}");
            firstRow = false;
        }
        dataJson.append("]");
        return dataJson.toString();
    }
}