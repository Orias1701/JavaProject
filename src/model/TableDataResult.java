package model;

import java.util.List;
import java.util.Map;

public class TableDataResult {
    public final List<Map<String, String>> data;
    public final Map<String, String> columnComments;
    public final String keyColumn;

    public TableDataResult(List<Map<String, String>> data, Map<String, String> columnComments, String keyColumn) {
        this.data = data;
        this.columnComments = columnComments;
        this.keyColumn = keyColumn;
    }
}