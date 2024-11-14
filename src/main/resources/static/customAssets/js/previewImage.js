function previewImage(input) {
    let file = input.files[0];

    // Проверка типа файла
    if (!file.type.match("image.*")) {
        showToast('error', 'Ошибка', 'Укажите фото');
        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return;
    }
    let mb = 7;

    if (file.size > mb * 1024 * 1024) {
        showToast('error', 'Ошибка', 'Укажите фото не превышающее ' + mb + ' МБ');
        // 500 килобайт в байтах
        input.classList.add("is-invalid");
        input.classList.remove("is-valid");
        return;
    }

    input.classList.add("is-valid");
    input.classList.remove("is-invalid");

    let reader = new FileReader();
    reader.onload = function (e) {
        let id = input.getAttribute('data-image-id');
        let image = document.getElementById(id);
        image.src = e.target.result;
        image.alt = "Предварительный просмотр";
        image.classList.add("img-preview");
    };
    reader.readAsDataURL(file);
}