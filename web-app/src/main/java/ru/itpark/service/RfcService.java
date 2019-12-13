package ru.itpark.service;

public interface RfcService {
    int getDownloadPercent();

    void downloadAllFromUrl(String numbers);
}
