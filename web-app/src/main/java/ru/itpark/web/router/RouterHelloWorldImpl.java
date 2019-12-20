package ru.itpark.web.router;

import lombok.val;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RouterHelloWorldImpl implements Router {

    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        try {
            val url = req.getRequestURI().substring(req.getContextPath().length());

            if (url.equals("/")) {
                if (req.getMethod().equals("GET")) {
                    String finalDateString = "2019/12/20 09:30:00"; //1d 3h correction
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("d");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
                    Date finalDate = sdf.parse(finalDateString);
                    long remainMillis = finalDate.getTime() - System.currentTimeMillis();
                    String timeRemain = sdf1.format(remainMillis) + "d " + sdf2.format(remainMillis);
                    req.setAttribute("timeRemain", timeRemain);
                    req.getRequestDispatcher("/WEB-INF/hello.jsp").forward(req, resp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}