package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.MyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RouterHelloWorldImpl implements Router {
    private MyService myService;

//    @Inject
//    public void setMyService(MyService myService) {
//        this.myService = myService;
//    }

    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        try {
            val url = req.getRequestURI().substring(req.getContextPath().length());

            if (url.equals("/")) {
                if (req.getMethod().equals("GET")) {
                    String finalDateString = "2019/12/21 11:00:00";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("d");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
                    Date finalDate = sdf.parse(finalDateString);
                    long remainMillis = finalDate.getTime() - System.currentTimeMillis();
                    String daysRemain = sdf1.format(remainMillis);
                    String timeRemain = sdf2.format(remainMillis);
                    req.setAttribute("daysRemain", daysRemain + "d");
                    req.setAttribute("timeRemain", timeRemain);
                    req.getRequestDispatcher("/WEB-INF/hello.jsp").forward(req, resp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}