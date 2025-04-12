// package model;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import util.Logger;

// public class JsonParser {

//     public static Map<String, String> parseTableInfo(String json) {
//         Map<String, String> tableInfo = new HashMap<>();
//         json = json.substring(1, json.length() - 1);
//         if (json.isEmpty()) return tableInfo;

//         String[] entries = json.split("},");
//         for (String entry : entries) {
//             entry = entry.replace("{", "").replace("}", "").trim();
//             String[] keyValuePairs = entry.split(",");
//             String name = "";
//             String comment = "";
//             for (String pair : keyValuePairs) {
//                 String[] parts = pair.split(":");
//                 String key = parts[0].replace("\"", "").trim();
//                 String value = parts[1].replace("\"", "").trim();
//                 if ("name".equals(key)) name = value;
//                 if ("comment".equals(key)) comment = value;
//             }
//             tableInfo.put(name, comment);
//         }
//         return tableInfo;
//     }

//     public static TableDataResult parseTableDataWithColumns(String json) {
//         List<Map<String, String>> data = new ArrayList<>();
//         Map<String, String> columnComments = new HashMap<>();

//         try {
//             json = json.trim();
//             if (!json.startsWith("{") || !json.endsWith("}")) {
//                 return new TableDataResult(data, columnComments);
//             }
//             json = json.substring(1, json.length() - 1);

//             String[] parts = json.split(",\"data\":");
//             if (parts.length != 2) {
//                 return new TableDataResult(data, columnComments);
//             }

//             String columnsJson = parts[0].replace("\"columns\":", "").trim();
//             columnsJson = columnsJson.substring(1, columnsJson.length() - 1);
//             if (!columnsJson.isEmpty()) {
//                 String[] columnEntries = columnsJson.split("},");
//                 for (String entry : columnEntries) {
//                     entry = entry.replace("{", "").replace("}", "").trim();
//                     String[] keyValuePairs = entry.split(",");
//                     String name = "";
//                     String comment = "";
//                     for (String pair : keyValuePairs) {
//                         String[] kv = pair.split(":");
//                         String key = kv[0].replace("\"", "").trim();
//                         String value = kv[1].replace("\"", "").trim();
//                         if ("name".equals(key)) name = value;
//                         if ("comment".equals(key)) comment = value;
//                     }
//                     columnComments.put(name, comment);
//                 }
//             }

//             String dataJson = parts[1].substring(1, parts[1].length() - 1);
//             if (!dataJson.isEmpty()) {
//                 String[] rows = dataJson.split("},");
//                 for (String row : rows) {
//                     row = row.replace("{", "").replace("}", "").trim();
//                     String[] keyValuePairs = row.split(",");
//                     Map<String, String> rowData = new HashMap<>();
//                     for (String pair : keyValuePairs) {
//                         String[] kv = pair.split(":");
//                         String key = kv[0].replace("\"", "").trim();
//                         String value = kv[1].replace("\"", "").trim();
//                         rowData.put(key, value);
//                     }
//                     data.add(rowData);
//                 }
//             }

//             return new TableDataResult(data, columnComments);
//         } catch (Exception e) {
//             Logger.logError("Error parsing table data: " + e.getMessage());
//             Logger.logStackTrace(e);
//             return new TableDataResult(data, columnComments);
//         }
//     }
// }