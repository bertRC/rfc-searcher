const delay = 200;

$(document).ready(searchProgressDoWork());

function setProgressValue(progressbar, pval) {
    progressbar.attr("aria-valuenow", pval);
    progressbar.css("width", pval + "%");
    progressbar.text(pval + "%");
}

function searchProgressDoWork() {
    $.get("/scriptHandler/searchProgress", function (resp) {
        var prgs = $(".progress");
        if (prgs.length !== 0) {
            var items = resp.split(",");
            var ids = items.map(item => item.split("=")[0]);
            var vals = items.map(item => item.split("=")[1]);

            for (var i = 0; i < prgs.length; i++) {
                var prg = prgs.eq(i);
                var row = prg.parent().parent();
                var rowId = row.attr("id");
                var stat = prg.parent().children("a");

                var index = ids.indexOf(rowId);
                if (index !== -1) {
                    if (vals[index] > 0 && vals[index] <= 100) {
                        var prgbar = prg.children();
                        stat.text("");
                        prg.css("display", "");
                        setProgressValue(prgbar, vals[index]);
                    }

                } else {
                    prg.remove();
                    $.get("/scriptHandler/getResult", {queryId: rowId}, function (resp) {
                        var lnk3 = row.children().eq(2).children();
                        if (resp !== "") {
                            stat.text("DONE");
                            lnk3.text("Download Results");
                            lnk3.attr("href", resp);
                        } else {
                            stat.text("UNKNOWN");
                            lnk3.remove();
                        }
                    });
                }
            }
            setTimeout(searchProgressDoWork, delay)
        }
    });
}