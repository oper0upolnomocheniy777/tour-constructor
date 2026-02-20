$(function() {
  $('#lang').click(function(e) {
    e.preventDefault();

    $.post('/lang', {lang: $(this).text().trim()})
     .done(function() {
      location.reload();
     });
  })

  hoverSwap($("#lang"));
});

function logout() {
    $.post('/logout')
     .done(function(data, textStatus, jqXHR) {
        location.href = '/login.html';
     })
}

function hoverSwap($el) {
    var swap = function(e) {
        var $span = $el.find('span');
        var currentText = $span.text();
        $span.text($el.data('alt'));
        $el.data('alt', currentText);
    };

    $el.hover(swap, swap);
}
