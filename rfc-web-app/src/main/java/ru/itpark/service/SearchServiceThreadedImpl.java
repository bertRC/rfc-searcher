package ru.itpark.service;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import ru.itpark.comparator.ResultsComparator;
import ru.itpark.enumeration.QueryStatus;
import ru.itpark.file.FileService;
import ru.itpark.model.ProgressValue;
import ru.itpark.model.QueryModel;
import ru.itpark.repository.QueryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SearchServiceThreadedImpl implements SearchService {
    private FileService fileService;
    private Path rfcPath;
    private QueryRepository queryRepository;

    private final int procCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService pool = Executors.newFixedThreadPool(procCount);
    private final ConcurrentMap<String, List<CompletableFuture<List<String>>>> initialFutures = new ConcurrentHashMap<>();

    private final Deque<QueryModel> currentQueries = new ConcurrentLinkedDeque<>();

    private final ConcurrentMap<String, ProgressValue> progress = new ConcurrentHashMap<>();

    private final ResultsComparator resultsComparator = new ResultsComparator();

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

    @Override
    public Map<String, String> getProgress() {
        return Maps.transformValues(progress, ProgressValue::getStringPercent);
    }

    private ProgressValue incProgressValue(String id) {
        final ProgressValue progressValue = progress.get(id);
        if (progressValue != null) {
            final int value = progressValue.getValue();
            progressValue.setValue(value + 1);
            return progress.put(id, progressValue);
        }
        return null;
    }

    private ProgressValue setProgressMaxValue(String id, int newMaxValue) {
        final ProgressValue progressValue = progress.get(id);
        if (progressValue != null) {
            progressValue.setMaxValue(newMaxValue);
            return progress.put(id, progressValue);
        }
        return null;
    }

    private void currentQueriesRemoveLastAndSave() {
        QueryModel last = currentQueries.peekLast();
        if (last == null) {
            return;
        }
        if (last.getStatus() == QueryStatus.ENQUEUED ||
                last.getStatus() == QueryStatus.INPROGRESS) {
            return;
        }
        currentQueries.remove(last);
        queryRepository.save(last);
        currentQueriesRemoveLastAndSave();
    }

    private CompletableFuture<List<String>> searchOneFuture(String id, String text, Path file) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> result = fileService.searchText(text, file);
            incProgressValue(id);
            return result;
        }, pool);
    }

    @Override
    public void search(String text) {
        System.out.println("Available Processors: " + procCount);
        System.out.println("Starting search: " + text);
        long startTime = System.currentTimeMillis();
        final String id = UUID.randomUUID().toString();
        QueryModel queryModel = new QueryModel(id, text, QueryStatus.ENQUEUED);
        progress.put(id, new ProgressValue());
        currentQueries.addFirst(queryModel);
        try {
            List<CompletableFuture<List<String>>> queryFutures = Files.list(rfcPath)
                    .filter(Files::isRegularFile)
                    .map(path -> searchOneFuture(id, text, path))
                    .collect(Collectors.toList());
            setProgressMaxValue(id, queryFutures.size());
            initialFutures.put(id, queryFutures);
            CompletableFuture<Void> queryTotal = CompletableFuture.allOf(
                    queryFutures.toArray(new CompletableFuture[0]));
            queryTotal.thenRun(() -> {
                List<String> result = new ArrayList<>();
                queryFutures.forEach(future -> {
                    List<String> list = future.join();
                    if (list != null) {
                        result.addAll(future.join());
                    }
                });
                result.sort(resultsComparator);
                fileService.writeResultFile(id + ".txt", text, result);
                queryModel.setStatus(QueryStatus.DONE);
                currentQueriesRemoveLastAndSave();
                initialFutures.remove(id);
                progress.remove(id);
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("Query: " + text + " " + id + "Searching took " + duration + " milliseconds");
            }).exceptionally(e -> {
                System.out.println("Searching was canceled: " + text + " " + id);
                queryModel.setStatus(QueryStatus.CANCELED);
                currentQueriesRemoveLastAndSave();
                initialFutures.remove(id);
                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelSearching(String id) {
        final List<CompletableFuture<List<String>>> futures = initialFutures.get(id);
        if (futures != null) {
            progress.remove(id);
            futures.forEach(future -> future.cancel(false));
        }

    }
}
