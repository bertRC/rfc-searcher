package ru.itpark.service;

import com.google.inject.Inject;
import ru.itpark.file.FileService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RfcServiceDefaultImpl implements RfcService {
    private FileService fileService;
    private Thread downloadThread;
    private final AtomicInteger downloadPercent = new AtomicInteger(-1); // -1 means that there is no downloading progress right now

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String getDownloadPercent() {
        return downloadPercent.toString();
    }

    @Override
    public void cancelDownloading() {
        if (downloadThread != null && !downloadThread.isInterrupted()) {
            downloadThread.interrupt();
            downloadPercent.set(-1);
        }
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

    @Override
    public void downloadAllFromUrl(String numbers) {
        long startTime = System.currentTimeMillis();
        if (downloadPercent.get() != -1) {
            // need to interrupt current working thread
            // and restart task as new
            cancelDownloading();
        }
        downloadPercent.set(0);
        downloadThread = new Thread(() -> {
            final String urlRegex = "https://tools.ietf.org/rfc/rfc%d.txt";
            final String fileNameRegex = "rfc%d.txt";
            final List<Integer> nums = parseIntToList(numbers);
            for (int i = 0; i < nums.size(); i++) {
                int index = nums.get(i);
                String fileName = String.format(fileNameRegex, index);
//                if (fileName.equals("rfc10.txt")) {
//                    Thread.currentThread().interrupt();
//                }
                String url = String.format(urlRegex, index);
                if (!fileService.downloadFromUrl(url, fileName, false)) {
                    System.out.println("Can not download " + fileName);
                }
                downloadPercent.set(100 * i / nums.size());
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Downloading was canceled on file: " + fileName);
                    break;
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Downloading took " + duration + " milliseconds");
            downloadPercent.set(-1);
        });
        downloadThread.start();
    }

}
