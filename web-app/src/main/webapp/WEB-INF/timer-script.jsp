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