package ru.itpark.web.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.MyService;
import ru.itpark.MyServiceImpl;
import ru.itpark.web.router.Router;
import ru.itpark.web.router.RouterDefaultImpl;
import ru.itpark.web.router.RouterHelloWorldImpl;

public class FrontControllerConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/files").with(FrontController.class);
                bind(Router.class).to(RouterDefaultImpl.class);
//                bind(MyService.class).to(MyServiceImpl.class);
            }
        });
    }
}
