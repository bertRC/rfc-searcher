<script>
    function startTimer() {
        var timer = document.getElementById("timer");
        var time = timer.innerHTML;
        var arr = time.split("d ");
        var dd = arr[0];
        var arr2 = arr[1].split(":");
        var hh = arr2[0];
        var mm = arr2[1];
        var ss = arr2[2];
        if (ss == 0) {
            if (mm == 0) {
                if (hh == 0) {
                    if (dd == 0) {
                        alert("Time is over");
                        window.location.reload();
                        return;
                    }
                    dd--;
                    hh = 24;
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
        document.getElementById("timer").innerHTML = dd + "d " + hh + ":" + mm + ":" + ss;
        setTimeout(startTimer, 1000);

    }
</script>