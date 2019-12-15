package ru.itpark.service;

import ru.itpark.model.QueryModel;

import java.util.List;

public interface RfcService {
    List<QueryModel> getAllQueries();

    void doSearch(String text);
}
