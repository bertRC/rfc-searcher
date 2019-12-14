<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RFC Searcher</title>
    <%@ include file="css-collection.jsp" %>
</head>
<body>
<div class="container">

    <nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e3f2fd;">
        <span class="navbar-brand mb-0 h1">RFC Searcher</span>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="/">Home <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Tasks</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                       data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Files
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <a class="dropdown-item" href="#" data-toggle="modal" data-target="#downloadModal">Download From
                            "tools.ietf.org/rfc"</a>
                        <a class="dropdown-item" href="<%= request.getContextPath()%>/rfc/?remove=all">Delete All</a>
                    </div>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0">
                <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">Search</button>
            </form>
        </div>
    </nav>

    <div class="row">
        <div class="col">
            <form action="<%= request.getContextPath() %>" method="post" enctype="multipart/form-data" class="mt-3">
                <div class="input-group">
                    <div class="custom-file">
                        <input type="file" name="rfcFile" multiple="multiple" class="custom-file-input"
                               id="inputGroupFile04" aria-describedby="inputGroupFileAddon04">
                        <label class="custom-file-label" for="inputGroupFile04">Choose files</label>
                    </div>
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary" type="submit" id="inputGroupFileAddon04">Upload
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <% String downloadProgress = (String) request.getAttribute("downloadProgress"); %>
    <% String pval = (downloadProgress != null) ? downloadProgress : "-1"; %>
    <% String visibility = "hidden"; %>
    <% if (!pval.equals("-1")) {
        visibility = "visible";
    } else {
        pval = "0";
    }%>
    <div class="progress" id="downloadProgress" style="visibility: <%= visibility %>">
        <div class="progress-bar progress-bar-striped progress-bar-animated" id="downloadProgressbar" role="progressbar"
             aria-valuenow="<%= pval %>"
             aria-valuemin="0" aria-valuemax="100" style="width: <%= pval %>%"><%= pval %>%
        </div>
    </div>

    <% List<String> fileNames = (List<String>) request.getAttribute("rfcFiles"); %>
    <% if (fileNames != null && !fileNames.isEmpty()) { %>
    <div class="row" style="margin-top: 16px">
        <div class="col">
            <table class="table table-sm" id="fileTable">
                <thead>
                <tr>
                    <th scope="col">Filename</th>
                    <th scope="col">Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (String fileName : fileNames) { %>
                <tr>
                    <td>
                        <a href="<%= request.getContextPath()%>/rfc/<%= fileName %>">
                            <%= fileName %>
                        </a>
                    </td>
                    <td>
                        <a href="<%= request.getContextPath()%>/rfc/?remove=<%= fileName %>">
                            Remove
                        </a>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <% } %>
</div>

<!-- Modal -->
<div class="modal fade" id="downloadModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
     aria-hidden="true">
    <form action="<%= request.getContextPath() %>/rfc/download" method="post" enctype="multipart/form-data">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Download From
                        "tools.ietf.org/rfc"</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="modal-body">
                    <div class="form-group">
                        <label for="numbers">Enter file numbers (e.g. 1, 2, 5-10)</label>
                        <input type="text" id="numbers" name="numbers" class="form-control" required value="1-200">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Download</button>
                </div>

            </div>
        </div>
    </form>
</div>

<%@ include file="script-collection.jsp" %>
<script>
    <%@ include file="js/download-progress.js" %>
</script>
<script>
    <%@ include file="js/file-table-init.js" %>
</script>
</body>
</html>
