package ru.itpark.service;

import ru.itpark.model.QueryModel;

import java.util.List;

public interface SearchService {
    List<QueryModel> getAllQueries();

    void doSearch(String text);
}
