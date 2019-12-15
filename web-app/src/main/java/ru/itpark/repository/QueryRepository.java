package ru.itpark.repository;

import ru.itpark.enumeration.QueryStatus;
import ru.itpark.model.QueryModel;
import ru.itpark.util.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class QueryRepository {
    private JdbcTemplate template = new JdbcTemplate();
    private final DataSource ds;

    public QueryRepository() {
        try {
            InitialContext context = new InitialContext();
            ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        try {
            template.update(ds, "CREATE TABLE IF NOT EXISTS queries (id TEXT PRIMARY KEY, query TEXT NOT NULL, status TEXT NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<QueryModel> getAll() {
        try {
            return template.queryForListReverseOrder(ds, "SELECT id, query, status FROM queries;", rs ->
                    new QueryModel(
                            rs.getString("id"),
                            rs.getString("query"),
                            QueryStatus.valueOf(rs.getString("status"))
                    )
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(QueryModel queryModel) {
        try {
            if (queryModel.getId() == null) {
                String id = UUID.randomUUID().toString();
                template.update(ds, "INSERT INTO queries(id, query, status) VALUES (?, ?, ?);", stmt -> {
                    stmt.setString(1, id);
                    stmt.setString(2, queryModel.getQuery());
                    stmt.setString(3, queryModel.getStatus().toString());
                    return stmt;
                });
                queryModel.setId(id);
            } else {
                template.update(ds, "UPDATE queries SET query = ?, status = ? WHERE id = ?;", stmt -> {
                    stmt.setString(1, queryModel.getQuery());
                    stmt.setString(2, queryModel.getStatus().toString());
                    stmt.setString(3, queryModel.getId());
                    return stmt;
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
