package co.edu.uniremington.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping("/api/h2-console")
public class H2ConsoleController {

    private final DataSource dataSource;

    public H2ConsoleController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody QueryRequest request) {
        try {
            Map<String, Object> result = new HashMap<>();

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                String sql = request.getSql().trim();

                if (sql.toLowerCase().startsWith("select")) {
                    ResultSet rs = stmt.executeQuery(sql);
                    result.put("data", convertResultSetToList(rs));
                    result.put("type", "SELECT");
                    result.put("success", true);
                } else if (sql.toLowerCase().startsWith("insert") ||
                           sql.toLowerCase().startsWith("update") ||
                           sql.toLowerCase().startsWith("delete")) {
                    int rowsAffected = stmt.executeUpdate(sql);
                    result.put("rowsAffected", rowsAffected);
                    result.put("type", "UPDATE");
                    result.put("success", true);
                } else {
                    result.put("success", false);
                    result.put("error", "Solo SELECT, INSERT, UPDATE y DELETE son permitidos");
                }
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getTables() {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> tables = new ArrayList<>();

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                String sql = "SELECT TABLE_NAME, TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'TABLE'";
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    Map<String, Object> table = new HashMap<>();
                    table.put("name", rs.getString("TABLE_NAME"));
                    table.put("schema", rs.getString("TABLE_SCHEMA"));
                    tables.add(table);
                }
            }

            result.put("tables", tables);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/table/{tableName}")
    public ResponseEntity<Map<String, Object>> getTableData(@PathVariable String tableName) {
        try {
            Map<String, Object> result = new HashMap<>();

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                String sql = "SELECT * FROM " + tableName;
                ResultSet rs = stmt.executeQuery(sql);
                result.put("data", convertResultSetToList(rs));
                result.put("tableName", tableName);
                result.put("success", true);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private List<Map<String, Object>> convertResultSetToList(ResultSet rs) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            list.add(row);
        }

        return list;
    }

    public static class QueryRequest {
        private String sql;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }
}
