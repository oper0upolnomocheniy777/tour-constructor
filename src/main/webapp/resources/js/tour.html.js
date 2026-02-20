$(function () {
  // Constants

  var $messages = $('#rating-messages');
  var mess = {
    0: '',
    1: $messages.find('input[name=one]').val(),
    2: $messages.find('input[name=two]').val(),
    3: $messages.find('input[name=three]').val(),
    4: $messages.find('input[name=four]').val(),
    5: $messages.find('input[name=five]').val(),
  };

  // DOM Cache

  var $imageTemplate = $('#image-template');
  $imageTemplate.removeAttr('id');

  var $uploadImagesAction = $('#upload-images-action');
  var $imagesInput = $('#images-input');
  var $imagesUploadForm = $('form#images-upload');

  var $tourImagesCreateForm = $('form#tour-images-create-form');
  var $tourImagesDeleteForm = $('form#tour-images-delete-form');

  var $uploadImagesModal = $('upload-images-modal');
  var $uploadImagesBtn = $('#upload-images-btn');


  var $submitReview = $("#submit-review");
  var $newReviewRating = $("#new-review-rating");

  // Logic

  replaceMarkdown();
  initReviews();
  var toolkit = initImagesLightbox();
  initImagesToolbar();
  initImagesUpload();
  useSubmitDivs($);

  function replaceMarkdown() {
    $('div[data-provide=markdown]').each(function() {
      var $this = $(this);
      $this.html(marked($this.html()));
    });
  }


  function initReviews() {
    $("#rating-static").rateYo({
      rating: $("#rating-val").val(),
      readOnly: true
    });

    $(".new-review-stars").rateYo({
      fullStars: true,
      precision: 0,

      onSet: function(rating) {
        $submitReview.removeAttr('disabled');
        $newReviewRating.val(rating);
      },

      onChange: function(rating, self) {
        $(this).next().text(mess[rating]);
      }
    });

    $("#new-review-btn").click(function() {
      $("#new-review").removeClass("hidden");
      $(this).hide();
    });
  }

  function initImagesLightbox() {
    var toolkit = $imagesInput.filer({
      addMore: true,
      fileMaxSize: 1,
      extensions: ["jpg", "png"],
      showThumbs: true,
    }).prop('jFiler');

    toolkit.reset();

    return toolkit;
  }

  function initImagesToolbar() {
    $('a[data-action=removeImage]').click(function() {
      $this = $(this);
      $form = $('#tour-image-remove-form');
      $form.find('input[name=id]').val($this.data('id'));
      silentSubmit($form, function() {
          $this.closest('.image-container').hide();
      });
    });
  }

  function initImagesUpload() {

    $imagesUploadForm.submit(function(e) {
        e.preventDefault();

        var formData = new FormData($(this)[0]);
        addFakeThumbs();

        $.ajax({
            url: $(this).attr('action'),
            type: 'POST',
            data: formData,
            async: true,
            success: function (data) {
                var jData = JSON.parse(data);

                for(var i = 0; i < jData.length; i++) {
                    var url = jData[i];

                    $tourImagesCreateForm.find('input[name=imageUrl]').val(url[0]);
                    $tourImagesCreateForm.find('input[name=thumbnailUrl]').val(url[1]);

                    silentSubmit($tourImagesCreateForm, fakeThumbsCompleted);
                }
            },
            cache: false,
            contentType: false,
            processData: false
        });

        return false;
      });

      $uploadImagesBtn.click(function() {
        $imagesUploadForm.submit();
        $uploadImagesModal.hide();
      });

  }

  var fakesQueue = [];

  function addFakeThumbs() {
    var count = toolkit.files_list.length;

    for(var i = 0; i < count; i++) {
        var clone = $imageTemplate.clone();
        fakesQueue.push(clone);
        $uploadImagesAction.before(clone);
        clone.removeClass('hidden');
    }
  }

  function fakeThumbsCompleted() {
    for(var i = 0; i < fakesQueue.length; i++) {
        var fake = fakesQueue[i];
        var $i = fake.find('i');
        $i.removeClass('glyphicon-open');
        $i.addClass('glyphicon-saved');
    }
    fakesQueue.length = 0;
  }
});
