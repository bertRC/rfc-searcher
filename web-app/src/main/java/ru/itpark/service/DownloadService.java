package ru.itpark.service;

public interface DownloadService {
    String getDownloadPercent();

    void cancelDownloading();

    void removeAllRfc();

    void downloadAllFromUrl(String numbers);
}
