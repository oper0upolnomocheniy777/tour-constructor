$(function() {
    var $message = $('#message');

    $('form').each(function(ind, el) {

        $el = $(el);
        $el.find('input[name=discount]').change((function($el) {
            return function() {
                $el.data('dirty', 'true');
            }
         })($el));
    });

    $('#save').click(function() {
        $('form').each(function(ind, form) {
            $form = $(form);
            if($form.data('dirty')) {
            	$form.data('dirty', false);
                $.post($form.attr('action'), $form.serialize())
                	.done(function() {
                	    showMessage('Success');
					})
					.fail( function() {
					    showMessage('Some errors occurred');
					});
					
            } else {
                showMessage('No changes');
            }
        });
    });

    $('div[data-tour-agent-action]').click(function (){
        var $this = $(this);
        $this.fadeOut(200);
        silentSubmit($this.closest('form'), function() {
            showMessage('Selected user has become a tour agent');
        });
    });

    function showMessage(msg) {
        $message.html(msg);
        $message.hide();
        $message.fadeIn();

        setTimeout(function() {
            $message.fadeOut();
        }, 3000);
    }
});