package ru.itpark.web.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.files.FileService;
import ru.itpark.files.MyFileServiceImpl;
import ru.itpark.web.router.Router;
import ru.itpark.web.router.RouterDefaultImpl;
import ru.itpark.web.router.RouterSessionInfoImpl;

public class FrontControllerConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/rfc/*").with(FrontController.class);
                bind(Router.class).to(RouterDefaultImpl.class);
                bind(FileService.class).to(MyFileServiceImpl.class);
//                bind(Router.class).to(RouterSessionInfoImpl.class);
//                bind(MyService.class).to(MyServiceImpl.class);
            }
        });
    }
}
