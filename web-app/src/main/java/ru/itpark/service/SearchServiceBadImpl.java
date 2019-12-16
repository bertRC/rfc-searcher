//package ru.itpark.service;
//
//import com.google.inject.Inject;
//import lombok.val;
//import ru.itpark.enumeration.QueryStatus;
//import ru.itpark.file.FileService;
//import ru.itpark.model.QueryModel;
//import ru.itpark.repository.QueryRepository;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Deque;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentLinkedDeque;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class SearchServiceBadImpl implements SearchService {
//    private FileService fileService;
//    private QueryRepository queryRepository;
//
//    private final ExecutorService pool = Executors.newFixedThreadPool(10);
////    private List<CompletableFuture<Void>> downloadFutures;
//
//    private final Deque<QueryModel> currentQueries = new ConcurrentLinkedDeque<>();
//
//    @Inject
//    public void setFileService(FileService fService) {
//        fileService = fService;
//        fileService.removeAllResults();
//    }
//
//    @Inject
//    public void setQueryRepository(QueryRepository qRepository) {
//        queryRepository = qRepository;
//        queryRepository.init();
//    }
//
//    @Override
//    public List<QueryModel> getAllQueries() {
//        List<QueryModel> result = new LinkedList<>(currentQueries);
//        result.addAll(queryRepository.getAll());
//        return result;
//    }
//
//    @Override
//    public void search(String text) {
//        currentQueries.addFirst(new QueryModel(
//                UUID.randomUUID().toString(),
//                text,
//                QueryStatus.ENQUEUED));
//
//    }
//
//    private void doSearch() {
//        if (searchThread != null && searchThread.isAlive()) {
//            //need to wait until done
////                searchThread.join();
//            return;
//        }
//        if (currentQueries.isEmpty()) {
//            return;
//        }
//        try {
//            QueryModel lastQuery = currentQueries.takeLast();
//            if (lastQuery.getStatus() != QueryStatus.ENQUEUED) {
//                throw new RuntimeException("Status must be ENQUEUED!");
//            }
//            lastQuery.setStatus(QueryStatus.INPROGRESS);
//            currentQueries.addLast(lastQuery);
//            taskPercent.put(lastQuery.getId(), 0);
//            searchThread = new Thread(() -> {
//                //searching in all files
//                String id = lastQuery.getId();
//                String text = lastQuery.getQuery();
//                        Path rfcPath = fileService.getRfcPath();
//                try (val paths = Files.list(rfcPath)) {
//                    long count = paths.count();
//                    //ищем text во всех файлах, формируем результат, сохраняем
//                    //обновляем проценты
//                    //проверяем на interrupt
//                    //под конец удаляем процент
//                    //делаем статус DONE
//                    //отправляем в БД
//                    //удаляем из currentQueries
//                    //searchThread = null;
//                    //запускаем doSearch
//                    //new Thread(this::doSearch).start();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            searchThread.start();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
