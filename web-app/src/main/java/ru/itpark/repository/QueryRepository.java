package ru.itpark.repository;

import ru.itpark.enumeration.QueryStatus;
import ru.itpark.model.QueryModel;
import ru.itpark.util.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

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

    private QueryStatus valueOf(String status) {
        switch (status) {
            case ("INPROGRESS"):
                return QueryStatus.INPROGRESS;
            case ("DONE"):
                return QueryStatus.DONE;
            case ("CANCELED"):
                return QueryStatus.CANCELED;
            default:
                return QueryStatus.ENQUEUED;
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
            return template.queryForList(ds, "SELECT id, query, status FROM queries;", rs ->
                    new QueryModel(
                            rs.getString("id"),
                            rs.getString("query"),
                            valueOf(rs.getString("status"))
                    )
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(QueryModel queryModel) {
//        try {
//            if (model.getId() == 0) {
//                int id = template.<Integer>updateForId(ds, "INSERT INTO autos(name, description, imageUrl) VALUES (?, ?, ?);", stmt -> {
//                    stmt.setString(1, model.getName());
//                    stmt.setString(2, model.getDescription());
//                    stmt.setString(3, model.getImageUrl());
//                    return stmt;
//                });
//                model.setId(id);
//            } else {
//                template.update(ds, "UPDATE autos SET name = ?, description = ?, imageUrl = ? WHERE id = ?;", stmt -> {
//                    stmt.setString(1, model.getName());
//                    stmt.setString(2, model.getDescription());
//                    stmt.setString(3, model.getImageUrl());
//                    stmt.setInt(4, model.getId());
//                    return stmt;
//                });
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }
}
