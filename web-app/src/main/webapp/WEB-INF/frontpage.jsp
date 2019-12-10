<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RFC Searcher</title>
    <%@ include file="bootstrap-css.jsp" %>
</head>
<body>

<div class="container">
    <div class="row mt-3">
        <div class="col">
            <h1 style="text-align: center">RFC Searcher</h1>
            Number of processors on the server: <%= Runtime.getRuntime().availableProcessors() %>
        </div>
    </div>
    <div class="row mt-3">
        <div class="col">
            <form class="mt-3" action="<%= request.getContextPath() %>/search/">
                <input name="text" class="form-control" type="search" placeholder="Search">
            </form>
        </div>
    </div>
    <div class="row mt-3">
        <div class="col">
            <form action="<%= request.getContextPath() %>" method="post" enctype="multipart/form-data" class="mt-3">
                <div class="input-group">
                    <div class="custom-file">
                        <input type="file" name="rfcFile" multiple="multiple" class="custom-file-input"
                               id="inputGroupFile04" aria-describedby="inputGroupFileAddon04">
                        <label class="custom-file-label" for="inputGroupFile04">Choose file</label>
                    </div>
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary" type="submit" id="inputGroupFileAddon04">Upload
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <% List<String> fileNames = (List<String>) request.getAttribute("rfcFiles"); %>
    <% if (fileNames != null && !fileNames.isEmpty()) { %>
    <div class="row mt-3">
        <div class="col">
            <table class="table table-sm">
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


<%@ include file="bootstrap-scripts.jsp" %>
</body>
</html>
