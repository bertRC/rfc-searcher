package ru.itpark.web.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.file.FileService;
import ru.itpark.file.FileServiceDefaultImpl;
import ru.itpark.repository.QueryRepository;
import ru.itpark.repository.QueryRepositorySqliteImpl;
import ru.itpark.service.DownloadService;
import ru.itpark.service.DownloadServiceDefaultImpl;
import ru.itpark.service.SearchService;
import ru.itpark.service.SearchServiceDefaultImpl;
import ru.itpark.web.router.Router;
import ru.itpark.web.router.RouterDefaultImpl;

public class FrontControllerConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/tasks", "/rfc*", "/scriptHandler/*").with(FrontController.class);
                bind(Router.class).to(RouterDefaultImpl.class);
//                bind(Router.class).to(RouterHelloWorldImpl.class);
                bind(SearchService.class).to(SearchServiceDefaultImpl.class);
                bind(DownloadService.class).to(DownloadServiceDefaultImpl.class);
                bind(FileService.class).to(FileServiceDefaultImpl.class);
                bind(QueryRepository.class).to(QueryRepositorySqliteImpl.class);
            }
        });
    }
}
