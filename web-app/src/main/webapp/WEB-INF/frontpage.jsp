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
            <form action="<%= request.getContextPath() %>" method="post" enctype="multipart/form-data" class="mt-3">
                <div class="input-group">
                    <div class="custom-file">
                        <input type="file" name="rfcfile" multiple="multiple" class="custom-file-input" id="inputGroupFile04" aria-describedby="inputGroupFileAddon04">
                        <label class="custom-file-label" for="inputGroupFile04">Choose file</label>
                    </div>
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary" type="submit" id="inputGroupFileAddon04">Upload</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>


<%@ include file="bootstrap-scripts.jsp" %>
</body>
</html>
