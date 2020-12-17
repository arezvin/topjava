$(function () {
    makeEditable({
            ajaxUrl: "ajax/meals/",
            datatableApi: $("#dataTable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "dateTime"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "calories"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            })
        }
    );
});

var formFilter;

function updateTableWithFilter() {
    $.ajax({
        type: "GET",
        url: context.ajaxUrl + "filter",
        data: formFilter.serialize(),
        success: function (data) {
            context.datatableApi.clear().rows.add(data).draw();
        }
    });
}

function resetFilter() {
    formFilter.find(":input").val("");
    updateTable();
}