package ru.itpark.service;

import ru.itpark.model.QueryModel;

import java.util.List;

public interface RfcService {
    List<QueryModel> getAllQueries();

    void doSearch(String text);

    String getDownloadPercent();

    void cancelDownloading();

    void removeAllRfc();

    void downloadAllFromUrl(String numbers);
}
