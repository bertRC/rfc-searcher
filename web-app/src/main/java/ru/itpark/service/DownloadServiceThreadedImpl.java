package ru.itpark.service;

import com.google.inject.Inject;
import ru.itpark.file.FileService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DownloadServiceThreadedImpl implements DownloadService {
    private FileService fileService;

    private final ExecutorService pool = Executors.newFixedThreadPool(40); //TODO: number of threads
    private List<CompletableFuture<Void>> futures;
    private final AtomicInteger tasksCompleted = new AtomicInteger(0);
    private final AtomicInteger tasksCount = new AtomicInteger(0);

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String getDownloadPercent() {
        return (tasksCount.get() == 0) ? "-1" : String.valueOf(100 * tasksCompleted.get() / tasksCount.get());
    }

    @Override
    public void cancelDownloading() {
        if (futures != null) {
            futures.forEach(future -> future.cancel(false));
        }
        tasksCount.set(0);
        tasksCompleted.set(0);
    }

    @Override
    public void removeAllRfc() {
        cancelDownloading();
        fileService.removeAll();
    }

    private List<Integer> parseIntToList(String str) {
        List<Integer> result = new ArrayList<>();
        Arrays.stream(str
                .replaceAll("\\s", "")
                .split(","))
                .forEach(s -> {
                    String[] range = s.split("-");
                    if (range.length == 1) {
                        try {
                            result.add(Integer.parseInt(s));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (range.length == 2) {
                        try {
                            int startIndex = Integer.parseInt(range[0]);
                            int endIndex = Integer.parseInt(range[1]);
                            for (int i = startIndex; i <= endIndex; i++) {
                                result.add(i);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return result;
    }

    private CompletableFuture<Void> downloadOneFuture(String url, String fileName, boolean replaceIfExists) {
        return CompletableFuture.runAsync(() -> {
            if (!fileService.downloadFromUrl(url, fileName, replaceIfExists)) {
                System.out.println("Can not download " + fileName);
            }
            tasksCompleted.incrementAndGet();
        }, pool);
    }

    @Override
    public void downloadAllFromUrl(String numbers, boolean replaceIfExists) {
        System.out.println("Starting download...");
        long startTime = System.currentTimeMillis();
        cancelDownloading();
        final String urlRegex = "https://tools.ietf.org/rfc/rfc%d.txt";
        final String fileNameRegex = "rfc%d.txt";
        final List<Integer> nums = parseIntToList(numbers);
        tasksCompleted.set(0);
        tasksCount.set(nums.size());
        futures = nums.stream().map(i -> {
            String fileName = String.format(fileNameRegex, i);
            String url = String.format(urlRegex, i);
            return downloadOneFuture(url, fileName, replaceIfExists);
        }).collect(Collectors.toList());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRun(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Downloading took " + duration + " milliseconds");
            tasksCount.set(0);
            tasksCompleted.set(0);
        }).exceptionally(e -> {
            System.out.println("Downloading was canceled");
            return null;
        });
    }
}
