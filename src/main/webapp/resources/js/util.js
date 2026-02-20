function useSubmitDivs($) {
    $('.submit').click(function() {
        $(this).closest('form').submit();
    })
}

function silentSubmit($el, onSuccess) {
    $.ajax({
        type: $el.attr('method'),
        url: $el.attr('action'),
        data: $el.serialize(),
        success: function (data) {
            console.log('success')
            if(onSuccess) {
                onSuccess(data);
            }
        }
    });
}