package ru.itpark.service;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.enumeration.QueryStatus;
import ru.itpark.file.FileService;
import ru.itpark.model.QueryModel;
import ru.itpark.repository.QueryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class SearchServiceDefaultImpl implements SearchService {
    private FileService fileService;
    private QueryRepository queryRepository;

    private Thread searchThread;
    private final BlockingDeque<QueryModel> currentQueries = new LinkedBlockingDeque<>();
    private final ConcurrentMap<String, Integer> taskPercent = new ConcurrentHashMap<>();

    @Inject
    public void setFileService(FileService fService) {
        fileService = fService;
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
    public Map<String, String> getAllTaskPercents() {
        HashMap<String, String> result = new HashMap<>();
        taskPercent.forEach((k, v) -> result.put(k, v.toString()));
        return result;
    }

    @Override
    public String getTaskPercent(String id) {
        return taskPercent.get(id).toString();
    }

    @Override
    public void search(String text) {
        currentQueries.addFirst(new QueryModel(
                UUID.randomUUID().toString(),
                text,
                QueryStatus.ENQUEUED));
        new Thread(this::doSearch).start();
    }

    private void doSearch() {
        if (searchThread != null && searchThread.isAlive()) {
            //need to wait until done
//                searchThread.join();
            return;
        }
        if (currentQueries.isEmpty()) {
            return;
        }
        try {
            QueryModel lastQuery = currentQueries.takeLast();
            if (lastQuery.getStatus() != QueryStatus.ENQUEUED) {
                throw new RuntimeException("Status must be ENQUEUED!");
            }
            lastQuery.setStatus(QueryStatus.INPROGRESS);
            currentQueries.addLast(lastQuery);
            taskPercent.put(lastQuery.getId(), 0);
            searchThread = new Thread(() -> {
                //searching in all files
                String id = lastQuery.getId();
                String text = lastQuery.getQuery();
                        Path rfcPath = fileService.getRfcPath();
                try (val paths = Files.list(rfcPath)) {
                    long count = paths.count();
                    //ищем text во всех файлах, формируем результат, сохраняем
                    //обновляем проценты
                    //проверяем на interrupt
                    //под конец удаляем процент
                    //делаем статус DONE
                    //отправляем в БД
                    //удаляем из currentQueries
                    //searchThread = null;
                    //запускаем doSearch
                    //new Thread(this::doSearch).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            searchThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
