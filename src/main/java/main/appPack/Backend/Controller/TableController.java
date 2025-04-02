package main.appPack.Backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import main.appPack.Backend.Service.TableService;

@RestController
@RequestMapping("/api")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping("/tables")
    public List<String> getTables() {
        return tableService.getTableNames();
    }
}