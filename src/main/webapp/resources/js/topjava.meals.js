var mealAjaxUrl = "profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: "profile/meals/filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get("profile/meals/", updateTableByData);
}

$.datetimepicker.setLocale('ru');
jQuery('#startDate,#endDate').datetimepicker({
    timepicker: false,
    format:'Y-m-d'
});
jQuery('#startTime,#endTime').datetimepicker({
    datepicker: false,
    format:'H:i'
});
jQuery('#dateTime').datetimepicker({
    format:'Y-m-d H:i'
});

$(function () {
    makeEditable({
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type, row) {
                        if(type === "display") {
                            return data.replace('T', ' ').substring(0, 16);
                        }
                        return data;
                    }
                },
                {
                    "data": "description",
                    "render": function (data, type, row) {
                        return data;
                    }
                },
                {
                    "data": "calories",
                    "render": function (data, type, row) {
                        return data;
                    }
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                if (!data.excess) {
                    $(row).attr("data-mealExcess", false);
                } else {
                    $(row).attr("data-mealExcess", true);
                }
            }
        }),
        updateTable: updateFilteredTable
    });
});