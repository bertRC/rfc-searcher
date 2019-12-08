package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.files.MyFileService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class RouterDefaultImpl implements Router {
    private MyFileService fileService;
//    public static final Pattern urlPattern = Pattern.compile("^/(.+)/(.*)$");

    @Inject
    public void setFileService(MyFileService fileService) {
        this.fileService = fileService;
    }

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

                if (req.getMethod().equals("POST")) {
                    val part = req.getPart("rfcfile");
                    fileService.writeFile(part);
                    resp.sendRedirect(rootUrl);
                    return;
                }

                throw new RuntimeException();
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
