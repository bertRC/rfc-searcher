package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.file.FileService;
import ru.itpark.service.RfcService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

public class RouterDefaultImpl implements Router {
    private FileService fileService;
    private RfcService rfcService;
//    public static final Pattern urlPattern = Pattern.compile("^/(.+)/(.*)$");

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Inject
    public void setRfcService(RfcService rfcService) {
        this.rfcService = rfcService;
    }

    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        try {
            val rootUrl = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
            val url = req.getRequestURI().substring(req.getContextPath().length());

            if (url.equals("/")) {
                if (req.getMethod().equals("GET")) {
                    val fileNames = fileService.getAll();
                    req.setAttribute("rfcFiles", fileNames);
                    req.setAttribute("downloadProgress", rfcService.getDownloadPercent());
                    req.getRequestDispatcher("/WEB-INF/newfrontpage.jsp").forward(req, resp);
                    return;
                }

                if (req.getMethod().equals("POST")) {
                    val parts = req.getParts().stream().filter(part -> part.getName().equals("rfcFile")).collect(Collectors.toList());
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

            // rfc1.txt or ?remove=rfc1.txt

            if (url.equals("/tasks/")) {
                if (req.getMethod().equals("GET")) {
                    req.getRequestDispatcher("/WEB-INF/tasks.jsp").forward(req, resp);
                    return;
                }
                throw new RuntimeException();
            }

            if (url.equals("/rfc/")) {
                if (req.getMethod().equals("GET")) {
                    val filename = req.getParameter("remove");
                    if (filename.toLowerCase().equals("all")) {
//                        fileService.removeAll();
                        rfcService.removeAllRfc();
                    } else {
                        fileService.removeFile(filename);
                    }
                    resp.sendRedirect(rootUrl);
                    return;
                }
                throw new RuntimeException();
            }

            if (url.startsWith("/rfc/")) {
                if (req.getMethod().equals("GET")) {
                    resp.setContentType("text/plain;charset=utf-8");
                    val filename = url.substring("/rfc/".length());
                    fileService.readFile(filename, resp.getWriter());
                    return;
                }

                if (url.equals("/rfc/download")) {
                    if (req.getMethod().equals("POST")) {
                        val numbers = req.getParameter("numbers");
                        rfcService.downloadAllFromUrl(numbers);
                        resp.sendRedirect(rootUrl);
                        return;
                    }
                }
                throw new RuntimeException();
            }

            if (url.equals("/scriptHandler/downloadProgress")) {
                val downloadPercent = rfcService.getDownloadPercent();
                resp.setContentType("text/plain");
                resp.getWriter().write(downloadPercent);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
