package ru.itpark.service;

public interface RfcService {
    String getDownloadPercent();

    void cancelDownloading();

    void removeAllRfc();

    void downloadAllFromUrl(String numbers);
}
