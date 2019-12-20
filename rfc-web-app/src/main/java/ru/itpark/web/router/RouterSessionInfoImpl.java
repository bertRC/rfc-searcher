package ru.itpark.web.router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class RouterSessionInfoImpl implements Router {
    @Override
    public void route(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        String message = "";

        String sessionId = session.getId();
        Date sessionCreationDate = new Date(session.getCreationTime());
        Date lastSessionAccess = new Date(session.getLastAccessedTime());
        String userId = "userId";

        if (session.isNew()) {
            message = "Welcome to this page";
        } else {
            message = "Glad to see You again";
        }

        resp.setContentType("text/html");

        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
            String title = "Session Tracking Demo";
            String docType = "<!DOCTYPE html>";

            writer.println(docType + "<html>" +
                    "<head>" +
                    "<title>" + title +
                    "</title>" +
                    "</head>" +
                    "<body>" +
                    message +
                    "<h1>Session Details</h1>" +
                    "Session ID:" + sessionId +
                    "<br/>" +
                    "Created: " + sessionCreationDate +
                    "<br/>" +
                    "Last Accessed Date: " + lastSessionAccess +
                    "<br/>" +
                    "User ID: " + userId +
                    "<br/>" +
                    "<br/>" +
                    "Server: Number of Processors: " + Runtime.getRuntime().availableProcessors() +
                    "</body>" +
                    "</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}