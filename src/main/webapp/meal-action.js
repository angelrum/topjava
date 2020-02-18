var button = document.querySelector(".insert");
var popup = document.querySelector(".b-popup");
var popup_close = document.getElementById("Cancel");
console.log(button);

button.onclick = function() {
    popup.classList.toggle("b-popup-on");
};
/*
popup_close.onclick = function(evt) {
    evt.preventDefault();
    popup.classList.toggle("b-popup-on");
};
*/