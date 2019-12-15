package ru.itpark.repository;

import ru.itpark.model.QueryModel;

import java.util.List;

public interface QueryRepository {
    void init();

    List<QueryModel> getAll();

    void save(QueryModel queryModel);
}
