<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Coming soon</title>
</head>
<body onload="startTimer()">
<h1>Hello Friend</h1>
<h2>What did you expect to see?</h2>
<h3>It will happen soon...</h3>
<p>
    <span id="timer" style="color: #4af; font-size: 150%; font-weight: bold;">${timeRemain}</span>
</p>
<script><%@ include file="js/timer-script.js" %></script>
</body>
</html>
