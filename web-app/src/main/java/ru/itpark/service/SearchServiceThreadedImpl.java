package ru.itpark.service;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.enumeration.QueryStatus;
import ru.itpark.file.FileService;
import ru.itpark.model.QueryModel;
import ru.itpark.repository.QueryRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchServiceThreadedImpl implements SearchService {
    private FileService fileService;
    private Path rfcPath;
    private QueryRepository queryRepository;

    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final Map<String, List<CompletableFuture<List<String>>>> globalFutures = new HashMap<>();

    private final Deque<QueryModel> currentQueries = new ConcurrentLinkedDeque<>();

    @Inject
    public void setFileService(FileService fService) {
        fileService = fService;
        rfcPath = fileService.getRfcPath();
        fileService.removeAllResults();
    }

    @Inject
    public void setQueryRepository(QueryRepository qRepository) {
        queryRepository = qRepository;
        queryRepository.init();
    }

    @Override
    public List<QueryModel> getAllQueries() {
        List<QueryModel> result = new LinkedList<>(currentQueries);
        result.addAll(queryRepository.getAll());
        return result;
    }

    private CompletableFuture<List<String>> searchOneFuture(String text, Path file) {
        return CompletableFuture.supplyAsync(() -> {
            //TODO: progress
            return fileService.searchText(text, file);
        }, pool)
                .exceptionally(e -> {
                    System.out.println("Some error happened in file: " + file.toString());
                    System.out.println(e.getMessage());
                    return null;
                });
    }

    @Override
    public void search(String text) {
        System.out.println("Starting search: " + text);
        long startTime = System.currentTimeMillis();
        QueryModel queryModel = new QueryModel(UUID.randomUUID().toString(), text, QueryStatus.INPROGRESS);
        currentQueries.addFirst(queryModel);
        //TODO: progress
        //параллельный поиск - два запроса можно обрабатывать одновременно
        try {
            List<CompletableFuture<List<String>>> queryFutures = Files.list(rfcPath)
                    .filter(Files::isRegularFile)
                    .map(path -> searchOneFuture(text, path))
                    .collect(Collectors.toList());
            globalFutures.put(queryModel.getId(), queryFutures);
            CompletableFuture<Void> queryTotal = CompletableFuture.allOf(
                    queryFutures.toArray(new CompletableFuture[0]));
            queryTotal.thenRun(() -> {
                //TODO: сортировка результата
                //TODO: запись результата в файл
                //TODO: запись в БД
                //TODO: завершить прогресс
                List<String> result = new ArrayList<>();
                queryFutures.forEach(future -> {
                    List<String> list = future.join();
                    if (list != null) {
                        result.addAll(future.join());
                    }
                });
                //sort
                fileService.writeResultFile(queryModel.getId() + ".txt", text, result);
                currentQueries.remove(queryModel);
                queryModel.setStatus(QueryStatus.DONE);
                queryRepository.save(queryModel);
                globalFutures.remove(queryModel.getId());
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("Searching took " + duration + " milliseconds");
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
