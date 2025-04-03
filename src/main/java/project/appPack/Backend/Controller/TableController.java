package project.appPack.Backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.appPack.Backend.Service.TableService;

@RestController
@RequestMapping("/api")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping("/tables")
    public List<Map<String, String>> getTables() {
        return tableService.getTableInfo();
    }

    @GetMapping("/tables/{tableName}")
    public List<Map<String, Object>> getTableData(@PathVariable String tableName) {
        return tableService.getTableData(tableName);
    }

    @PostMapping("/tables/{tableName}")
    public void addRow(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        tableService.addRow(tableName, data);
    }

    @PutMapping("/tables/{tableName}/{idColumn}/{idValue}")
    public void updateRow(@PathVariable String tableName, @PathVariable String idColumn, 
                         @PathVariable Object idValue, @RequestBody Map<String, Object> data) {
        tableService.updateRow(tableName, idColumn, idValue, data);
    }

    @DeleteMapping("/tables/{tableName}/{idColumn}/{idValue}")
    public void deleteRow(@PathVariable String tableName, @PathVariable String idColumn, 
                         @PathVariable Object idValue) {
        tableService.deleteRow(tableName, idColumn, idValue);
    }
}