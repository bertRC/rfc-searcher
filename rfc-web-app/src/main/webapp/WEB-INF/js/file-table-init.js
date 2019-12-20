$(document).ready(function () {
    $('#fileTable').dataTable({
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
        pageLength: 20,
        ordering: false,
        searching: false
    });
});