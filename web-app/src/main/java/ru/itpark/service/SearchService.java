package ru.itpark.service;

import ru.itpark.model.QueryModel;

import java.util.List;
import java.util.Map;

public interface SearchService {
    List<QueryModel> getAllQueries();

    Map<String, String> getProgress();

    void search(String text);

    void cancelSearching(String id);
}
