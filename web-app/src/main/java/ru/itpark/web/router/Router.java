package ru.itpark.web.router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Router {
    void route(HttpServletRequest req, HttpServletResponse resp);
}
