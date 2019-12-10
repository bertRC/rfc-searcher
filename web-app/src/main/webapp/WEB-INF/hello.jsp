<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%--    <script src="js/timer.js" type="text/javascript"></script>--%>
    <script>
        function startTimer() {
            var timer = document.getElementById("timer");
            var time = timer.innerHTML;
            var arr = time.split(":");
            var hh = arr[0];
            var mm = arr[1];
            var ss = arr[2];
            if (ss == 0) {
                if (mm == 0) {
                    if (hh == 0) {
                        alert("Time is up");
                        window.location.reload();
                        return;
                    }
                    hh--;
                    mm = 60;
                    if (hh < 10)
                        hh = "0" + hh;
                }
                mm--;
                if (mm < 10)
                    mm = "0" + mm;
                ss = 59;
            } else ss--;
            if (ss < 10)
                ss = "0" + ss;
            document.getElementById("timer").innerHTML = hh + ":" + mm + ":" + ss;
            setTimeout(startTimer, 1000);
        }
    </script>
    <title>Coming soon</title>
</head>
<body onload="startTimer()">
<h1>Hello Friend</h1>
<h2>What did you expect to see?</h2>
<h3>It will happen soon...</h3>
<p><span id="day" style="color: #4af; font-size: 150%; font-weight: bold;">${daysRemain}</span>
    <span id="timer" style="color: #4af; font-size: 150%; font-weight: bold;">${timeRemain}</span></p>
</body>
</html>
