const delay = 200;

$(document).ready(downloadProgressInit());

function setProgressValue(progressbar, pval) {
    progressbar.attr("aria-valuenow", pval);
    progressbar.css("width", pval + "%");
    progressbar.text(pval + "%");
}

function downloadProgressInit() {
    $.get("/scriptHandler/downloadProgress", function (resp) {
        if (resp >= 0 && resp < 100) {
            setTimeout(downloadProgressDoWork, delay);
        }
    });
}

function downloadProgressDoWork() {
    $.get("/scriptHandler/downloadProgress", function (resp) {
        if (resp >= 0 && resp < 100) {
            setProgressValue($("#downloadProgressbar"), resp);
            setTimeout(downloadProgressDoWork, delay);
        } else {
            downloadProgressComplete();
        }
    });
}

function downloadProgressComplete() {
    window.location.reload();
}