package ru.itpark.util;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class JdbcTemplate {
    public <T> List<T> queryForList(DataSource dataSource, String query, RowMapper<T> mapper) throws SQLException {
        return execute(dataSource, query, (Executable<List<T>>) resultSet -> {
            List<T> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(mapper.map(resultSet));
            }
            return list;
        });
    }

    public <T> List<T> queryForList(DataSource dataSource, String query, RowMapper<T> mapper, PreparedStatementSetter setter) throws SQLException {
        return execute(dataSource, query, setter, resultSet -> {
            List<T> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(mapper.map(resultSet));
            }
            return list;
        });
    }

    public <T> Optional<T> queryForObject(DataSource dataSource, String query, RowMapper<T> mapper) throws SQLException {
        return execute(dataSource, query, (Executable<Optional<T>>) resultSet -> {
            if (resultSet.next()) {
                return Optional.of(mapper.map(resultSet));
            }
            return Optional.empty();
        });
    }

    public <T> Optional<T> queryForObject(DataSource dataSource, String query, PreparedStatementSetter setter, RowMapper<T> mapper) throws SQLException {
        return execute(dataSource, query, setter, resultSet -> {
            if (resultSet.next()) {
                return Optional.of(mapper.map(resultSet));
            }
            return Optional.empty();
        });
    }

    public int update(DataSource dataSource, String query) throws SQLException {
        return execute(dataSource, query);
    }

    public int update(DataSource dataSource, String query, PreparedStatementSetter setter) throws SQLException {
        return execute(dataSource, query, setter);
    }

    public <T> T updateForId(DataSource dataSource, String query) throws SQLException {
        return executeWithId(dataSource, query);
    }

    public <T> T updateForId(DataSource dataSource, String query, PreparedStatementSetter setter) throws SQLException {
        return executeWithId(dataSource, query, setter);
    }

    private int execute(DataSource dataSource, String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            return statement.executeUpdate(query);
        }
    }

    private int execute(DataSource dataSource, String query, PreparedStatementSetter setter) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = setter.set(connection.prepareStatement(query));
        ) {
            return statement.executeUpdate();
        }
    }

    private <T> T execute(DataSource dataSource, String query, Executable<T> function) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
        ) {
            return function.execute(resultSet);
        }
    }

    private <T> T execute(DataSource dataSource, String query, PreparedStatementSetter setter, Executable<T> function) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = setter.set(connection.prepareStatement(query));
                ResultSet resultSet = statement.executeQuery();
        ) {
            return function.execute(resultSet);
        }
    }

    private <T> T executeWithId(DataSource dataSource, String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return (T) generatedKeys.getObject(1);
            }

            throw new SQLException("No keys generated");
        }
    }

    private <T> T executeWithId(DataSource dataSource, String query, PreparedStatementSetter setter) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = setter.set(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS));
        ) {
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return (T) generatedKeys.getObject(1);
            }

            throw new SQLException("No keys generated");
        }
    }
}
