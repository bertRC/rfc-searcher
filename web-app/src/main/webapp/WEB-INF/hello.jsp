<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="timer-script.jsp" %>
    <script src="http://code.jquery.com/jquery-2.2.4.js" type="text/javascript"></script>
    <title>Coming soon</title>
</head>
<body onload="startTimer()">
<h1>Hello Friend</h1>
<h2>What did you expect to see?</h2>
<h3>It will happen soon...</h3>
<p>
    <span id="timer" style="color: #4af; font-size: 150%; font-weight: bold;">${timeRemain}</span>
</p>
</body>
</html>
