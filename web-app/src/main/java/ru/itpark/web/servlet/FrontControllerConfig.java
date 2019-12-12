package ru.itpark.web.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.file.FileService;
import ru.itpark.file.FileServiceDefaultImpl;
import ru.itpark.service.RfcService;
import ru.itpark.service.RfcServiceDefaultImpl;
import ru.itpark.web.router.Router;
import ru.itpark.web.router.RouterDefaultImpl;
import ru.itpark.web.router.RouterHelloWorldImpl;

public class FrontControllerConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/rfc/*", "/scriptHandler").with(FrontController.class);
//                bind(Router.class).to(RouterDefaultImpl.class);
//                bind(Router.class).to(RouterSessionInfoImpl.class);
                bind(Router.class).to(RouterHelloWorldImpl.class);
//                bind(FileService.class).to(FileServiceDefaultImpl.class);
//                bind(RfcService.class).to(RfcServiceDefaultImpl.class);
            }
        });
    }
}
