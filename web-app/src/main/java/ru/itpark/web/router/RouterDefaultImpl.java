package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.files.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.List;
import java.util.stream.Collectors;

public class RouterDefaultImpl implements Router {
    private FileService fileService;
//    public static final Pattern urlPattern = Pattern.compile("^/(.+)/(.*)$");

    @Inject
    public void setFileService(FileService fileService) {
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
//                    val part = req.getPart("rfcfile");
//                    fileService.writeFile(part);
                    List<Part> parts = req.getParts().stream().filter(part -> part.getName().equals("rfcfile")).collect(Collectors.toList());
                    fileService.writeFiles(parts);
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
