package ru.itpark.web.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.file.FileService;
import ru.itpark.file.FileServiceBufferedImpl;
import ru.itpark.file.FileServiceDefaultImpl;
import ru.itpark.repository.QueryRepository;
import ru.itpark.repository.QueryRepositorySqliteImpl;
import ru.itpark.repository.QueryRepositorySqliteSynchronizedImpl;
import ru.itpark.service.*;
import ru.itpark.web.router.Router;
import ru.itpark.web.router.RouterDefaultImpl;
import ru.itpark.web.router.RouterHelloWorldImpl;
import ru.itpark.web.router.RouterRegexImpl;

public class FrontControllerConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/tasks", "/search", "/rfc*", "/results*", "/query", "/scriptHandler/*").with(FrontController.class);
//                bind(Router.class).to(RouterDefaultImpl.class);
                bind(Router.class).to(RouterRegexImpl.class);
//                bind(Router.class).to(RouterHelloWorldImpl.class);
                bind(SearchService.class).to(SearchServiceThreadedImpl.class);
//                bind(DownloadService.class).to(DownloadServiceDefaultImpl.class);
                bind(DownloadService.class).to(DownloadServiceThreadedImpl.class);
//                bind(FileService.class).to(FileServiceDefaultImpl.class);
                bind(FileService.class).to(FileServiceBufferedImpl.class);
//                bind(QueryRepository.class).to(QueryRepositorySqliteImpl.class);
                bind(QueryRepository.class).to(QueryRepositorySqliteSynchronizedImpl.class);
            }
        });
    }
}
