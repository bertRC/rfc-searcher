<%@ page import="ru.itpark.model.QueryModel" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.itpark.enumeration.QueryStatus" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RFC Searcher</title>
    <%@ include file="css-collection.jsp" %>
</head>
<body>
<div class="container">

    <nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e3f2fd;">
        <a class="navbar-brand" href="/">RFC Searcher</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath()%>/tasks">Tasks</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button"
                       data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Files
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <a class="dropdown-item" href="/" data-toggle="modal" data-target="#downloadModal">Download From
                            "tools.ietf.org/rfc"</a>
                        <a class="dropdown-item" href="<%= request.getContextPath()%>/rfc?remove=all">Remove All</a>
                    </div>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0" action="<%= request.getContextPath() %>/search">
                <input name="text" class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"
                       pattern=".{3,}" required title="3 characters minimum" autofocus>
                <button class="btn btn-outline-primary my-2 my-sm-0" type="submit">Search</button>
            </form>
        </div>
    </nav>

    <% List<QueryModel> queries = (List<QueryModel>) request.getAttribute("queries"); %>
    <% if (queries != null && !queries.isEmpty()) { %>
    <div class="row" style="margin-top: 16px">
        <div class="col">
            <table class="table table-sm table-bordered" id="queryTable">
                <thead class="thead-light">
                <tr>
                    <th scope="col">Search Query</th>
                    <th scope="col">Query Status</th>
                    <th scope="col">Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (QueryModel query : queries) { %>
                <tr>
                    <td><%= query.getQuery() %>
                    </td>
                    <td><%= query.getStatus() %>
                        <%--                        <div class="progress" style="display: none">--%>
                        <%--                            <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar"--%>
                        <%--                                 aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" style="width: 25%">25%--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>
                    </td>
                    <td>
                        <% if (query.getStatus() == QueryStatus.DONE) { %>
                        <a href="<%= request.getContextPath()%>/results/<%= query.getId() + ".txt" %>">
                            Download Results
                        </a>
                        <% } %>
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
<%--<script>--%>
<%--    <%@ include file="js/download-progress.js" %>--%>
<%--</script>--%>
<script>
    <%@ include file="js/query-table-init.js" %>
</script>
</body>
</html>
