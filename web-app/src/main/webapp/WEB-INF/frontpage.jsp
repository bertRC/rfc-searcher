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
            <a class="btn btn-primary" href="/files/" role="button">Manage Files</a>
        </div>
    </div>
</div>


<%@ include file="bootstrap-scripts.jsp" %>
</body>
</html>
