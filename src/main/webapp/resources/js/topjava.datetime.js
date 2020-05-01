var dateformat = 'Y-m-d';
var timeformat = 'H:i';

jQuery("#startDate").datetimepicker({
    timepicker:false,
    format:dateformat
});
jQuery("#endDate").datetimepicker({
    timepicker:false,
    format:dateformat
});
jQuery("#startTime").datetimepicker({
    datepicker: false,
    format:timeformat
});

jQuery("#endTime").datetimepicker({
    datepicker: false,
    format:timeformat
});

$.datetimepicker.setLocale('ru');