function addNewImageItem(entityGroupName) {

    let items = document.querySelectorAll('.image-item');
    let lastItem = items[items.length - 1];
    let hiddenInput;
    if (lastItem !== undefined) {
        hiddenInput = lastItem.querySelector('input[type=hidden]').value != null
            ? parseInt(lastItem.querySelector('input[type=hidden]').value)+1 : 1;
    } else {
        hiddenInput = 1
    }

    let newImageItem = '<div class="col-3 image-item" th:each="imageItem, i : *{'+entityGroupName+'}">\n' +
        '  <div class="card text-center mb-2">\n' +
        '      <div style="object-fit : contain" class="card-header">\n' +
        '         <img src="https://via.placeholder.com/300x200?text=Нет изображения"\n' +
        '              id="'+entityGroupName+items.length+'" \n' +
        '              class="card-img-top" style="object-fit : cover; height: 200px">\n' +
        '      </div>\n' +
        '      <div class="card-body">\n' +
        '          <input type=hidden class="form-control"\n' +
        '                 name="'+entityGroupName+'['+items.length+'].id" \n' +
        '                 id="'+entityGroupName+items.length+'.id" \n' +
        '                 multiple value="">\n' +
        '          <input type="file" class="form-control"\n' +
        '                 id="newImageItem" \n' +
        '                 data-image-id="'+entityGroupName+items.length+'" \n' +
        '                 accept="image/png, image/jpeg"\n' +
        '                 required onchange="previewImage(this)"\n' +
        '                 multiple name="'+entityGroupName+'['+items.length+'].url" />\n' +
        '          <div class="form-group mb-1">\n' +
        '              <button style="width: 100%;"\n' +
        '               type="button"\n' +
        '               class="btn btn-danger" onclick="removeImageBlock(this, \'.image-item\')">\n' +
        '               Удалить\n' +
        '              </button>\n' +
        '          </div>\n' +
        '      </div>\n' +
        '  </div>\n' +
        '</div>';



    let galleryImage = document.getElementById('gallery-image');
    $(newImageItem).appendTo(galleryImage);

}

function removeImageBlock(thisElement) {
    let elementBanner = thisElement.closest('.image-item');
    elementBanner.remove();
}