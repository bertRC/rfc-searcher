<script src="http://code.jquery.com/jquery-2.2.4.js" type="text/javascript"></script>
<script>
    function startTimer() {
        $.ajax({
            url: 'scriptHandler',
            success: function (responseText) {
                $('#timer').text(responseText);
                setTimeout(startTimer, 1000);
            }
        });
    }
</script>