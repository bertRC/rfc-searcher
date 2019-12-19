package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.file.FileService;
import ru.itpark.service.DownloadService;
import ru.itpark.service.SearchService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

public class RouterDefaultImpl implements Router {
    private FileService fileService;
    private SearchService searchService;
    private DownloadService downloadService;
//    public static final Pattern urlPattern = Pattern.compile("^/(.+)/(.*)$");
//    TODO: regex
    //TODO: Tasks Files -> Files Tasks navbar

    @Inject
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Inject
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Inject
    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
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
                    req.setAttribute("downloadProgress", downloadService.getDownloadPercent());
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

            if (url.equals("/tasks")) {
                if (req.getMethod().equals("GET")) {
                    val queries = searchService.getAllQueries();
                    req.setAttribute("queries", queries);
                    req.setAttribute("searchingProgress", searchService.getProgress());
                    req.getRequestDispatcher("/WEB-INF/tasks.jsp").forward(req, resp);
                    return;
                }
                throw new RuntimeException();
            }

            if (url.equals("/rfc")) {
                if (req.getMethod().equals("GET")) {
                    val filename = req.getParameter("remove");
                    if (filename.toLowerCase().equals("all")) {
//                        fileService.removeAll();
                        downloadService.removeAllRfc();
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
                    fileService.readRfcFile(filename, resp.getWriter());
                    return;
                }

                if (url.equals("/rfc/download")) {
                    if (req.getMethod().equals("POST")) {
                        val numbers = req.getParameter("numbers");
                        downloadService.downloadAllFromUrl(numbers, false);
                        resp.sendRedirect(rootUrl);
                        return;
                    }
                }
                if (url.equals("/rfc/cancel")) {
                    if (req.getMethod().equals("POST")) {
                        downloadService.cancelDownloading();
                        resp.sendRedirect(rootUrl);
                        return;
                    }
                }
                throw new RuntimeException();
            }

            if (url.equals("/search")) {
                if (req.getMethod().equals("GET")) {
                    val text = req.getParameter("text");
                    //TODO: search
                    searchService.search(text);
                    resp.sendRedirect("/tasks");
                    return;
                }
                throw new RuntimeException();
            }

            if (url.equals("/query")) {
                if (req.getMethod().equals("GET")) {
                    val id = req.getParameter("cancel");
                    searchService.cancelSearching(id);
                    resp.sendRedirect("/tasks");
                    return;
                }
                throw new RuntimeException();
            }

            if (url.startsWith("/results/")) {
                if (req.getMethod().equals("GET")) {
                    resp.setContentType("text/plain;charset=utf-8");
                    val filename = url.substring("/results/".length());
                    fileService.readResultsFile(filename, resp.getWriter());
                    return;
                }
                throw new RuntimeException();
            }

            if (url.equals("/scriptHandler/downloadProgress")) {
                val downloadPercent = downloadService.getDownloadPercent();
                resp.setContentType("text/plain");
                resp.getWriter().write(downloadPercent);
                return;
            }

            if (url.equals("/scriptHandler/searchProgress")) {
                val searchProgress = searchService.getProgress().toString()
                        .replaceAll("\\s", "")
                        .replace("{", "")
                        .replace("}", "");
                resp.setContentType("text/plain");
                resp.getWriter().write(searchProgress);
//                System.out.println(searchProgress);
                return;
            }

            if (url.equals("/scriptHandler/getResult")) {
                val queryId = req.getParameter("queryId");
                val resultFile = fileService.isResultReady(queryId);
//                if (!resultFile.equals("")) {
//                    resultFile = "/results/" + resultFile;
//                }
                resp.getWriter().write(resultFile);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
