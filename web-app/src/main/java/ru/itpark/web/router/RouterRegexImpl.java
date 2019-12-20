package ru.itpark.web.router;

import com.google.inject.Inject;
import lombok.val;
import ru.itpark.file.FileService;
import ru.itpark.service.DownloadService;
import ru.itpark.service.SearchService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RouterRegexImpl implements Router {
    private FileService fileService;
    private SearchService searchService;
    private DownloadService downloadService;
    public static final Pattern urlPattern = Pattern.compile("^/(\\w+)/?(.*)$");
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

            val matcher = urlPattern.matcher(url);
            String firstUrlSubPath;
            String secondUrlSubPath;
            if (matcher.find()) {
                firstUrlSubPath = matcher.group(1);
                secondUrlSubPath = matcher.group(2);
            } else return;

            if (firstUrlSubPath.equals("tasks")) {
                if (req.getMethod().equals("GET")) {
                    val queries = searchService.getAllQueries();
                    req.setAttribute("queries", queries);
                    req.setAttribute("searchingProgress", searchService.getProgress());
                    req.getRequestDispatcher("/WEB-INF/tasks.jsp").forward(req, resp);
                    return;
                }
                throw new RuntimeException();
            }

            if (firstUrlSubPath.equals("rfc")) {
                if (secondUrlSubPath.isEmpty() && req.getMethod().equals("GET")) {
                    val filename = req.getParameter("remove");
                    if (filename.toLowerCase().equals("all")) {
                        downloadService.removeAllRfc();
                    } else {
                        fileService.removeFile(filename);
                    }
                    resp.sendRedirect(rootUrl);
                    return;
                }

                if (!secondUrlSubPath.isEmpty() && req.getMethod().equals("GET")) {
                    resp.setContentType("text/plain;charset=utf-8");
                    fileService.readRfcFile(secondUrlSubPath, resp.getWriter());
                    return;
                }

                if (secondUrlSubPath.equals("download") && req.getMethod().equals("POST")) {
                    val numbers = req.getParameter("numbers");
                    downloadService.downloadAllFromUrl(numbers, false);
                    resp.sendRedirect(rootUrl);
                    return;
                }

                if (secondUrlSubPath.equals("cancel") && req.getMethod().equals("POST")) {
                    downloadService.cancelDownloading();
                    resp.sendRedirect(rootUrl);
                    return;
                }

                throw new RuntimeException();
            }

            if (firstUrlSubPath.equals("search")) {
                if (req.getMethod().equals("GET")) {
                    val text = req.getParameter("text").trim();
                    searchService.search(text);
                    resp.sendRedirect("/tasks");
                    return;
                }
                throw new RuntimeException();
            }

            if (firstUrlSubPath.equals("query")) {
                if (req.getMethod().equals("GET")) {
                    val id = req.getParameter("cancel");
                    searchService.cancelSearching(id);
                    resp.sendRedirect("/tasks");
                    return;
                }
                throw new RuntimeException();
            }

            if (firstUrlSubPath.equals("results")) {
                if (req.getMethod().equals("GET")) {
                    resp.setContentType("text/plain;charset=utf-8");
                    fileService.readResultsFile(secondUrlSubPath, resp.getWriter());
                    return;
                }
                throw new RuntimeException();
            }

            if (firstUrlSubPath.equals("scriptHandler")) {
                if (secondUrlSubPath.equals("downloadProgress")) {
                    val downloadPercent = downloadService.getDownloadPercent();
                    resp.setContentType("text/plain");
                    resp.getWriter().write(downloadPercent);
                    return;
                }

                if (secondUrlSubPath.equals("searchProgress")) {
                    val searchProgress = searchService.getProgress().toString()
                            .replaceAll("\\s", "")
                            .replace("{", "")
                            .replace("}", "");
                    resp.setContentType("text/plain");
                    resp.getWriter().write(searchProgress);
                    return;
                }

                if (secondUrlSubPath.equals("getResult")) {
                    val queryId = req.getParameter("queryId");
                    val resultFile = fileService.isResultReady(queryId);
                    resp.getWriter().write(resultFile);
                    return;
                }
            }

            throw new RuntimeException();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
