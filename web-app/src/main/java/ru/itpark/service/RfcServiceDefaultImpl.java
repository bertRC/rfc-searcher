package ru.itpark.service;

import com.google.inject.Inject;
import ru.itpark.enumeration.QueryStatus;
import ru.itpark.file.FileService;
import ru.itpark.model.QueryModel;
import ru.itpark.repository.QueryRepository;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class RfcServiceDefaultImpl implements RfcService {
    private FileService fileService;
    private QueryRepository queryRepository;

    private Thread searchThread;
    //    private final List<QueryModel> currentQueries = new LinkedList<>();
    private final BlockingDeque<QueryModel> currentQueries = new LinkedBlockingDeque<>();

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
    public void doSearch(String text) {
        currentQueries.addFirst(new QueryModel(
                UUID.randomUUID().toString(),
                text,
                QueryStatus.ENQUEUED));
        if (searchThread == null || !searchThread.isAlive()) {
            //можно делать работу, а иначе запрос просто повисит в currentQueries
        }
    }
}
