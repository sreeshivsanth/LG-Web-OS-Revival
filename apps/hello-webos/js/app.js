(function () {
    'use strict';
    var buttons = [document.getElementById('hello'), document.getElementById('reset')];
    var status = document.getElementById('status');
    var count = 0;
    buttons[0].addEventListener('click', function () {
        count += 1;
        status.textContent = 'Hello from your TV! Greetings sent: ' + count + '.';
    });
    buttons[1].addEventListener('click', function () {
        count = 0;
        status.textContent = 'Ready when you are. Press OK to say hello.';
    });
    document.addEventListener('keydown', function (event) {
        var key = event.keyCode;
        var index = buttons.indexOf(document.activeElement);
        if (key >= 37 && key <= 40) {
            event.preventDefault();
            index = index < 0 ? 0 : (index + ((key === 37 || key === 38) ? -1 : 1) + buttons.length) % buttons.length;
            buttons[index].focus();
        } else if (key === 13 && index >= 0) {
            event.preventDefault();
            if (!event.repeat) { buttons[index].click(); }
        }
        // Back (461) is intentionally unhandled: webOS's default history/exit
        // behavior owns it. Do not intercept it or call unsupported exit APIs.
    });
    buttons[0].focus();
}());
