package model;

import controller.LogHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    public Map<String, String> parseTableInfo(String json) {
        Map<String, String> tableInfo = new LinkedHashMap<>();
        json = json.substring(1, json.length() - 1);
        if (json.isEmpty()) return tableInfo;

        String[] entries = json.split("},");
        for (String entry : entries) {
            entry = entry.replace("{", "").replace("}", "").trim();
            String[] keyValuePairs = entry.split(",");
            String name = "";
            String comment = "";
            for (String pair : keyValuePairs) {
                String[] parts = pair.split(":");
                String key = parts[0].replace("\"", "").trim();
                String value = parts[1].replace("\"", "").trim();
                if ("name".equals(key)) name = value;
                if ("comment".equals(key)) comment = value;
            }
            tableInfo.put(name, comment);
        }
        return tableInfo;
    }

    public ApiClient.TableDataResult parseTableDataWithColumns(String json) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> columnComments = new LinkedHashMap<>();
        Map<String, String> columnTypes = new LinkedHashMap<>();
        String keyColumn = "";
        List<String> primaryKeyColumns = new ArrayList<>();

        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                LogHandler.logError("Invalid JSON format");
                return new ApiClient.TableDataResult(data, columnComments, columnTypes, keyColumn, primaryKeyColumns);
            }
            json = json.substring(1, json.length() - 1);

            String[] parts = json.split(",\"columns\":");
            if (parts.length != 2) {
                LogHandler.logError("Invalid JSON structure");
                return new ApiClient.TableDataResult(data, columnComments, columnTypes, keyColumn, primaryKeyColumns);
            }

            String[] headerParts = parts[0].split(",\"primaryKeyColumns\":");
            String keyColumnJson = headerParts[0].replace("\"keyColumn\":", "").trim();
            if (keyColumnJson.startsWith("\"") && keyColumnJson.endsWith("\"")) {
                keyColumn = keyColumnJson.substring(1, keyColumnJson.length() - 1);
            }

            String primaryKeysJson = headerParts[1].substring(0, headerParts[1].indexOf(",\"data\":"));
            primaryKeysJson = primaryKeysJson.trim();
            if (primaryKeysJson.startsWith("[") && primaryKeysJson.endsWith("]")) {
                primaryKeysJson = primaryKeysJson.substring(1, primaryKeysJson.length() - 1);
                if (!primaryKeysJson.isEmpty()) {
                    String[] keys = primaryKeysJson.split(",");
                    for (String key : keys) {
                        primaryKeyColumns.add(key.replace("\"", "").trim());
                    }
                }
            }

            String columnsJson = parts[1].substring(0, parts[1].indexOf(",\"data\":"));
            columnsJson = columnsJson.trim();
            if (columnsJson.startsWith("[") && columnsJson.endsWith("]")) {
                columnsJson = columnsJson.substring(1, columnsJson.length() - 1);
                if (!columnsJson.isEmpty()) {
                    String[] columnEntries = columnsJson.split("},");
                    for (String entry : columnEntries) {
                        entry = entry.replace("{", "").replace("}", "").trim();
                        String[] keyValuePairs = entry.split(",");
                        String name = "";
                        String comment = "";
                        String dataType = "";
                        for (String pair : keyValuePairs) {
                            String[] kv = pair.split(":");
                            String key = kv[0].replace("\"", "").trim();
                            String value = kv[1].replace("\"", "").trim();
                            if ("name".equals(key)) name = value;
                            if ("comment".equals(key)) comment = value;
                            if ("dataType".equals(key)) dataType = value;
                        }
                        columnComments.put(name, comment);
                        columnTypes.put(name, dataType);
                    }
                }
            }

            String dataJson = parts[1].substring(parts[1].indexOf(",\"data\":") + 8);
            dataJson = dataJson.substring(1, dataJson.length() - 1);
            if (!dataJson.isEmpty()) {
                String[] rows = dataJson.split("},");
                for (String row : rows) {
                    row = row.replace("{", "").replace("}", "").trim();
                    String[] keyValuePairs = row.split(",");
                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (String pair : keyValuePairs) {
                        String[] kv = pair.split(":");
                        String key = kv[0].replace("\"", "").trim();
                        String value = kv[1].replace("\"", "").trim();
                        rowData.put(key, value);
                    }
                    data.add(rowData);
                }
            }

            return new ApiClient.TableDataResult(data, columnComments, columnTypes, keyColumn, primaryKeyColumns);
        } catch (Exception e) {
            LogHandler.logError("Error parsing table data: " + e.getMessage(), e);
            return new ApiClient.TableDataResult(data, columnComments, columnTypes, keyColumn, primaryKeyColumns);
        }
    }

    public String mapToJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(value).append("\"");
            }
            if (i < data.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("}");
        return json.toString();
    }

    public Map<String, Object> parseLoginResponse(String json) {
        Map<String, Object> result = new HashMap<>();
        try {
            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                LogHandler.logError("Invalid login JSON format");
                return result;
            }
            json = json.substring(1, json.length() - 1);
            String[] parts = json.split(",\"permissions\":");
            if (parts.length != 2) {
                LogHandler.logError("Invalid login JSON structure");
                return result;
            }

            String[] headerParts = parts[0].split(",");
            for (String part : headerParts) {
                String[] kv = part.split(":");
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                result.put(key, value);
            }

            String permissionsJson = parts[1].substring(0, parts[1].length() - 1);
            Map<String, String> permissions = new HashMap<>();
            permissionsJson = permissionsJson.substring(1, permissionsJson.length() - 1);
            String[] permPairs = permissionsJson.split(",");
            for (String pair : permPairs) {
                String[] kv = pair.split(":");
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                permissions.put(key, value);
            }
            result.put("permissions", permissions);
        } catch (Exception e) {
            LogHandler.logError("Error parsing login response: " + e.getMessage(), e);
        }
        return result;
    }
}