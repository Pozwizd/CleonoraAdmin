var i = -1;
var toastCount = 0;
var $toastlast;
var getMessage = function () {
    var msgs = [

    ];
    i++;
    if (i === msgs.length) {
        i = 0;
    }
    return msgs[i];
};

function showToast(shortCutFunction, title, msg) {
    var isRtl = $('html').attr('dir') === 'rtl',

        toastIndex = toastCount++,
        prePositionClass = 'toast-top-right';

    prePositionClass =
        typeof toastr.options.positionClass === 'undefined' ? 'toast-top-right' : toastr.options.positionClass;

    toastr.options = {
        maxOpened: 1,
        autoDismiss: true,
        closeButton: true,
        debug: $('#debugInfo').prop('checked'),
        newestOnTop: $('#newestOnTop').prop('checked'),
        progressBar: $('#progressBar').prop('checked'),
        positionClass: $('#positionGroup input:radio:checked').val() || 'toast-top-right',
        preventDuplicates: $('#preventDuplicates').prop('checked'),
        onclick: null,
        rtl: isRtl
    };

    //Add fix for multiple toast open while changing the position
    if (prePositionClass != toastr.options.positionClass) {
        toastr.options.hideDuration = 0;
        toastr.clear();
    }

    if ($('#addBehaviorOnToastClick').prop('checked')) {
        toastr.options.onclick = function () {
            alert('You can perform some custom action after a toast goes away');
        };
    }
    if ($('#addBehaviorOnToastCloseClick').prop('checked')) {
        toastr.options.onCloseClick = function () {
            alert('You can perform some custom action when the close button is clicked');
        };
    }

    toastr.options.showDuration = 300;
    toastr.options.hideDuration = 1000;
    toastr.options.timeOut = 5000;
    toastr.options.extendedTimeOut = 1000;
    toastr.options.showEasing = "swing";
    toastr.options.hideEasing = "linear";
    toastr.options.showMethod = "fadeIn";
    toastr.options.hideMethod = "fadeOut";


    var $toast = toastr[shortCutFunction](msg, title); // Wire up an event handler to a button in the toast, if it exists
    $toastlast = $toast;
    if (typeof $toast === 'undefined') {
        return;
    }
    if ($toast.find('#okBtn').length) {
        $toast.delegate('#okBtn', 'click', function () {
            alert('you clicked me. i was toast #' + toastIndex + '. goodbye!');
            $toast.remove();
        });
    }
    if ($toast.find('#surpriseBtn').length) {
        $toast.delegate('#surpriseBtn', 'click', function () {
            alert('Surprise! you clicked me. i was toast #' + toastIndex + '. You could perform an action here.');
        });
    }
    if ($toast.find('.clear').length) {
        $toast.delegate('.clear', 'click', function () {
            toastr.clear($toast, {
                force: true
            });
        });
    }
}