<script>
    const delay = 200;

    $(document).ready(downloadProgressInit());

    function setProgressValue(progressbar, pval) {
        progressbar.attr('aria-valuenow', pval);
        progressbar.css('width', pval + '%');
        progressbar.text(pval + '%');
    }

    function downloadProgressInit() {
        $.ajax({
            url: '/scriptHandler/downloadProgress',
            success: function (responseText) {
                if (responseText >= 0 && responseText < 100) {
                    // initialization
                    $('#downloadProgress').css('visibility', 'visible');
                    setProgressValue($('#downloadProgressbar'), responseText);
                    setTimeout(downloadProgressDoWork, delay);
                }
            }
        });
    }

    function downloadProgressDoWork() {
        $.ajax({
            url: '/scriptHandler/downloadProgress',
            success: function (responseText) {
                if (responseText >= 0 && responseText < 100) {
                    setProgressValue($('#downloadProgressbar'), responseText);
                    setTimeout(downloadProgressDoWork, delay);
                } else {
                    downloadProgressComplete();
                }
            }
        });
    }

    function downloadProgressComplete() {
        $('#downloadProgress').css('visibility', 'hidden');
        setProgressValue($('#downloadProgressbar'), 0);
        window.location.reload();
    }
</script>