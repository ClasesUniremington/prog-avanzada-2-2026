package co.edu.uniremington.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class H2ConsoleConfiguration {

    @Bean
    public ServletRegistrationBean<H2ConsoleWebServlet> h2ConsoleInfo() {
        ServletRegistrationBean<H2ConsoleWebServlet> registration =
            new ServletRegistrationBean<>(new H2ConsoleWebServlet());
        registration.addUrlMappings("/h2-console", "/h2-console/");
        registration.setLoadOnStartup(1);
        return registration;
    }

    static class H2ConsoleWebServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>H2 Database Console</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            padding: 20px;
                        }
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                            display: grid;
                            grid-template-columns: 250px 1fr;
                            gap: 20px;
                        }
                        .sidebar {
                            background: white;
                            border-radius: 10px;
                            padding: 20px;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                            height: fit-content;
                        }
                        .main {
                            background: white;
                            border-radius: 10px;
                            padding: 30px;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        h1 { color: #333; margin-bottom: 20px; font-size: 24px; }
                        h2 { color: #667eea; margin: 20px 0 10px; font-size: 16px; }
                        .table-list {
                            list-style: none;
                        }
                        .table-list li {
                            padding: 10px;
                            margin: 5px 0;
                            background: #f0f0f0;
                            border-radius: 5px;
                            cursor: pointer;
                            transition: all 0.3s;
                        }
                        .table-list li:hover {
                            background: #667eea;
                            color: white;
                            transform: translateX(5px);
                        }
                        .editor-section {
                            margin-bottom: 20px;
                        }
                        textarea {
                            width: 100%;
                            height: 150px;
                            padding: 15px;
                            border: 2px solid #ddd;
                            border-radius: 5px;
                            font-family: 'Courier New', monospace;
                            font-size: 13px;
                            resize: vertical;
                        }
                        textarea:focus {
                            outline: none;
                            border-color: #667eea;
                            box-shadow: 0 0 10px rgba(102, 126, 234, 0.1);
                        }
                        .button-group {
                            display: flex;
                            gap: 10px;
                            margin-top: 10px;
                        }
                        button {
                            padding: 10px 20px;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            font-weight: bold;
                            transition: all 0.3s;
                        }
                        .btn-execute {
                            background: #667eea;
                            color: white;
                            flex: 1;
                        }
                        .btn-execute:hover {
                            background: #5568d3;
                            transform: translateY(-2px);
                            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                        }
                        .btn-clear {
                            background: #f0f0f0;
                            color: #333;
                        }
                        .btn-clear:hover {
                            background: #e0e0e0;
                        }
                        .results {
                            margin-top: 20px;
                            background: #f9f9f9;
                            padding: 15px;
                            border-radius: 5px;
                            border: 1px solid #ddd;
                        }
                        .results.hidden { display: none; }
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 10px;
                            font-size: 13px;
                        }
                        th, td {
                            padding: 12px;
                            text-align: left;
                            border-bottom: 1px solid #ddd;
                        }
                        th {
                            background: #667eea;
                            color: white;
                            font-weight: bold;
                        }
                        tr:hover { background: #f5f5f5; }
                        .error {
                            background: #fee;
                            border: 1px solid #fcc;
                            color: #c33;
                            padding: 15px;
                            border-radius: 5px;
                            margin-top: 10px;
                        }
                        .success {
                            background: #efe;
                            border: 1px solid #cfc;
                            color: #3c3;
                            padding: 15px;
                            border-radius: 5px;
                            margin-top: 10px;
                        }
                        .info {
                            background: #eef;
                            border: 1px solid #ccf;
                            color: #33c;
                            padding: 10px;
                            border-radius: 5px;
                            margin-top: 10px;
                            font-size: 12px;
                        }
                        .loading { color: #667eea; font-style: italic; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="sidebar">
                            <h2>📊 Tablas</h2>
                            <ul class="table-list" id="tableList">
                                <li style="color: #999;">Cargando...</li>
                            </ul>
                        </div>
                        <div class="main">
                            <h1>🗄️ H2 Database Console</h1>

                            <div class="editor-section">
                                <h2>Ejecutar SQL</h2>
                                <textarea id="sqlEditor" placeholder="SELECT * FROM users;"></textarea>
                                <div class="button-group">
                                    <button class="btn-execute" onclick="executeSql()">▶ Ejecutar</button>
                                    <button class="btn-clear" onclick="clearEditor()">✕ Limpiar</button>
                                </div>
                            </div>

                            <div id="resultsContainer" class="results hidden">
                                <h2>Resultados</h2>
                                <div id="resultContent"></div>
                            </div>
                        </div>
                    </div>

                    <script>
                        loadTables();

                        function loadTables() {
                            fetch('/api/h2-console/tables')
                                .then(res => res.json())
                                .then(data => {
                                    const list = document.getElementById('tableList');
                                    list.innerHTML = '';

                                    if (data.tables && data.tables.length > 0) {
                                        data.tables.forEach(table => {
                                            const li = document.createElement('li');
                                            li.textContent = table.name;
                                            li.onclick = () => loadTableData(table.name);
                                            list.appendChild(li);
                                        });
                                    } else {
                                        list.innerHTML = '<li style="color: #999;">No hay tablas</li>';
                                    }
                                })
                                .catch(err => {
                                    document.getElementById('tableList').innerHTML = '<li style="color: red;">Error: ' + err.message + '</li>';
                                });
                        }

                        function loadTableData(tableName) {
                            document.getElementById('sqlEditor').value = 'SELECT * FROM ' + tableName + ';';
                            executeSql();
                        }

                        function executeSql() {
                            const sql = document.getElementById('sqlEditor').value.trim();
                            if (!sql) {
                                alert('Ingresa una consulta SQL');
                                return;
                            }

                            const resultContainer = document.getElementById('resultsContainer');
                            const resultContent = document.getElementById('resultContent');
                            resultContent.innerHTML = '<p class="loading">Ejecutando...</p>';
                            resultContainer.classList.remove('hidden');

                            fetch('/api/h2-console/query', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ sql: sql })
                            })
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    if (data.type === 'SELECT' && data.data && data.data.length > 0) {
                                        let html = '<table><thead><tr>';
                                        const keys = Object.keys(data.data[0]);
                                        keys.forEach(key => html += '<th>' + key + '</th>');
                                        html += '</tr></thead><tbody>';
                                        data.data.forEach(row => {
                                            html += '<tr>';
                                            keys.forEach(key => html += '<td>' + (row[key] || '-') + '</td>');
                                            html += '</tr>';
                                        });
                                        html += '</tbody></table>';
                                        resultContent.innerHTML = html + '<div class="info">📊 ' + data.data.length + ' filas</div>';
                                    } else if (data.type === 'UPDATE') {
                                        resultContent.innerHTML = '<div class="success">✓ ' + data.rowsAffected + ' filas afectadas</div>';
                                        loadTables();
                                    } else if (data.type === 'SELECT') {
                                        resultContent.innerHTML = '<div class="info">📊 0 filas retornadas</div>';
                                    }
                                } else {
                                    resultContent.innerHTML = '<div class="error">❌ Error: ' + data.error + '</div>';
                                }
                            })
                            .catch(err => {
                                resultContent.innerHTML = '<div class="error">❌ Error: ' + err.message + '</div>';
                            });
                        }

                        function clearEditor() {
                            document.getElementById('sqlEditor').value = '';
                            document.getElementById('sqlEditor').focus();
                        }

                        document.getElementById('sqlEditor').addEventListener('keydown', e => {
                            if (e.ctrlKey && e.key === 'Enter') executeSql();
                        });
                    </script>
                </body>
                </html>
                """);
        }
    }
}
