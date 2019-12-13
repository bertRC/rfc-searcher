package ru.itpark.service;

import com.google.inject.Inject;
import ru.itpark.file.FileService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RfcServiceDefaultImpl implements RfcService {
    private FileService fileService;
private final AtomicInteger downloadPercent = new AtomicInteger(0);

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public int getDownloadPercent() {
        return downloadPercent.get();
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
        final String urlRegex = "https://tools.ietf.org/rfc/rfc%d.txt";
        final String fileNameRegex = "rfc%d.txt";
        final List<Integer> nums = parseIntToList(numbers);
        nums.forEach(i -> {
            String fileName = String.format(fileNameRegex, i);
            String url = String.format(urlRegex, i);
            if (!fileService.downloadFromUrl(url, fileName)) {
                System.out.println("Can not download " + fileName);
            }
        });
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Downloading took " + duration + " milliseconds");
    }

}
