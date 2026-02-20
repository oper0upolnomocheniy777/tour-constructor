$(function() {
    var sortGlyph = 'glyphicon-sort-by-order';

    $('div[data-sort-btn]').each(function() {
        $this = $(this);
        $ico = $this.find('i');
        $inp = $this.find('input');

        $ico.addClass('glyphicon');

        updateView($this, $ico, $inp);

        $this.click((function($this, $ico, $inp) {
            return function() {
                updateData($this);
                updateView($this, $ico, $inp);
            }
        })($this, $ico, $inp));
    });

    function updateData($this) {
        $this.data('sort-btn', nextStatus($this.data('sort-btn')));
    }

    function updateView($this, $ico, $inp) {
        var status = $this.data('sort-btn');

        if (status !== '') {
            $this.addClass('active')
            $ico.removeClass('glyphicon-sort');

            if (status === 'asc') {
                $ico.removeClass(sortGlyph + '-alt');
                $ico.addClass(sortGlyph);
            } else {
                $ico.removeClass(sortGlyph);
                $ico.addClass(sortGlyph + '-alt');
            }
        } else {
            $this.removeClass('active');
            $ico.removeClass(sortGlyph);
            $ico.removeClass(sortGlyph + '-alt');
            $ico.addClass('glyphicon-sort');
        }

        $inp.val(status);
    }

    function nextStatus(status) {
        if (status === '') {
            return 'asc';
        } else if (status === 'asc') {
            return 'desc';
        } else {
            return '';
        }
    }
});