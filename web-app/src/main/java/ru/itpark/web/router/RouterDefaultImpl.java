package ru.itpark.web.router;

import lombok.val;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class RouterDefaultImpl implements Router {
//    public static final Pattern urlPattern = Pattern.compile("^/(.+)/(.*)$");

    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        try {
            val rootUrl = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
            val url = req.getRequestURI().substring(req.getContextPath().length());

            if (url.equals("/")) {
                if (req.getMethod().equals("GET")) {
                    req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
                    return;
                }
            }

            if (url.equals("/files")) {
                if (req.getMethod().equals("GET")) {
                    req.getRequestDispatcher("/WEB-INF/filespage.jsp").forward(req, resp);
                    return;
                }

            }


//            val matcher = urlPattern.matcher(url);
//            String queryName;
//            String attribute;
//            // url template: "/queryName/attribute"
//            if (matcher.find()) {
//                queryName = matcher.group(1);
//                attribute = matcher.group(2);
//            } else return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
