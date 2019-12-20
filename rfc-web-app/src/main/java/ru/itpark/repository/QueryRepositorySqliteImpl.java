package ru.itpark.repository;

import ru.itpark.enumeration.QueryStatus;
import ru.itpark.exception.DataAccessException;
import ru.itpark.model.QueryModel;
import ru.itpark.util.JdbcHelper;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class QueryRepositorySqliteImpl implements QueryRepository {
    private JdbcHelper jdbcHelper = new JdbcHelper();
    private final DataSource ds;

    public QueryRepositorySqliteImpl() {
        try {
            InitialContext context = new InitialContext();
            ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        } catch (NamingException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void init() {
        try {
            jdbcHelper.update(ds, "CREATE TABLE IF NOT EXISTS queries (id TEXT PRIMARY KEY, query TEXT NOT NULL, status TEXT NOT NULL)");
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<QueryModel> getAll() {
        try {
            return jdbcHelper.queryForListReverseOrder(ds, "SELECT id, query, status FROM queries;", rs ->
                    new QueryModel(
                            rs.getString("id"),
                            rs.getString("query"),
                            QueryStatus.valueOf(rs.getString("status"))
                    )
            );
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void save(QueryModel queryModel) {
        try {
            jdbcHelper.update(ds, "INSERT INTO queries(id, query, status) VALUES (?, ?, ?);", stmt -> {
                stmt.setString(1, queryModel.getId());
                stmt.setString(2, queryModel.getQuery());
                stmt.setString(3, queryModel.getStatus().toString());
                return stmt;
            });
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
    //TODO: think about remove methods
}
