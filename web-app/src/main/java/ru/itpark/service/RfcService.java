package ru.itpark.service;

public interface RfcService {
    String getDownloadPercent();

    void downloadAllFromUrl(String numbers);
}
