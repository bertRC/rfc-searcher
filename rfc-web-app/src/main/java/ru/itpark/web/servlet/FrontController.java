package ru.itpark.web.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ru.itpark.web.router.Router;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class FrontController extends HttpServlet {
    private Router router;

    @Inject
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
//        log("service");
        router.route(req, resp);
    }
}
