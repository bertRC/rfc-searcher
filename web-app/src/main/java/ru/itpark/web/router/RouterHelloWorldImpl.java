package ru.itpark.web.router;

import com.google.inject.Inject;
import ru.itpark.MyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RouterHelloWorldImpl implements Router {
    private MyService myService;

    @Inject
    public void setMyService(MyService myService) {
        this.myService = myService;
    }

    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getMethod().equals("GET")) {
            try {
                req.setAttribute("text", myService.printMessage());
                req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
