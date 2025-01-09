/**
 * GenericAPIService
 *
 * Обрабатывает все взаимодействия с API, включая операции CRUD.
 */
class GenericAPIService {
    /**
     * Конструктор класса, инициализирует базовый URL и конечные точки из конфигурации.
     *
     * @param {Object} config - Объект конфигурации, содержащий baseURL и endpoints.
     * @param {string} entityType - Тип сущности ('json' или 'formdata'). По умолчанию 'json'.
     */
    constructor(config, entityType = 'json') {
        this.baseURL = config.baseURL;
        this.endpoints = config.endpoints;
        this.entityType = entityType;
    }

    /**
     * Выполняет HTTP-запрос к указанной конечной точке с заданными опциями.
     *
     * @param {string} endpoint - Конечная точка API для запроса.
     * @param {Object} options - Опции Fetch API (метод, заголовки, тело и т.д.).
     * @returns {Promise<Object|null>} - Парсенный JSON-ответ или null.
     * @throws {Object} - Бросает объект ошибки, содержащий статус и данные.
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                const errorData = await this._parseErrorResponse(response);
                throw { status: response.status, data: errorData };
            }
            return await this._parseResponse(response);
        } catch (error) {
            throw error;
        }
    }

    /**
     * Парсит успешный ответ на основе Content-Type.
     *
     * @param {Response} response - Объект Response Fetch API.
     * @returns {Promise<Object|null>} - Парсенные данные JSON или null.
     */
    async _parseResponse(response) {
        const contentType = response.headers.get('Content-Type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return null;
    }

    /**
     * Парсит ошибочный ответ, предоставляя сообщение по умолчанию, если парсинг не удался.
     *
     * @param {Response} response - Объект Response Fetch API.
     * @returns {Promise<Object>} - Парсенные данные ошибки или сообщение по умолчанию.
     */
    async _parseErrorResponse(response) {
        try {
            return await response.json();
        } catch {
            return { message: 'Неизвестная ошибка' };
        }
    }

    /**
     * Получает все сущности с пагинацией и опциональным поисковым запросом.
     *
     * @param {number} page - Номер страницы для получения.
     * @param {number} size - Количество элементов на странице.
     * @param {string} search - Строка поискового запроса.
     * @returns {Promise<Object>} - Ответ API, содержащий контент и информацию о пагинации.
     */
    getAll(page = 0, size = 5, search = '') {
        const encodedSearch = encodeURIComponent(search);
        const endpoint = `${this.endpoints.getAll}?page=${page}&size=${size}&search=${encodedSearch}`;
        return this.request(endpoint);
    }

    /**
     * Получает одну сущность по ее ID.
     *
     * @param {number|string} id - ID сущности для получения.
     * @returns {Promise<Object>} - Ответ API, содержащий данные сущности.
     */
    getById(id) {
        const endpoint = this.endpoints.getById.replace('{id}', id);
        return this.request(endpoint);
    }

    /**
     * Создает новую сущность с предоставленными данными.
     *
     * @param {Object} entityData - Данные сущности для создания.
     * @param {string} entityType - Тип сущности ('json' или 'formdata'). Если не указан, используется тип из конструктора.
     * @returns {Promise<Object>} - Ответ API, содержащий созданную сущность.
     */
    create(entityData, entityType = this.entityType) {
        const options = this._prepareBody('POST', entityData, entityType);
        return this.request(this.endpoints.create, options);
    }

    /**
     * Обновляет существующую сущность по ее ID с предоставленными данными.
     *
     * @param {number|string} id - ID сущности для обновления.
     * @param {Object} entityData - Новые данные для сущности.
     * @param {string} entityType - Тип сущности ('json' или 'formdata'). Если не указан, используется тип из конструктора.
     * @returns {Promise<Object>} - Ответ API, содержащий обновленную сущность.
     */
    update(id, entityData, entityType = this.entityType) {
        const endpoint = this.endpoints.update.replace('{id}', id);
        const options = this._prepareBody('PUT', entityData, entityType);
        return this.request(endpoint, options);
    }

    /**
     * Удаляет сущность по ее ID.
     *
     * @param {number|string} id - ID сущности для удаления.
     * @returns {Promise<Object>} - Ответ API, подтверждающий удаление.
     */
    delete(id) {
        const endpoint = this.endpoints.delete.replace('{id}', id);
        return this.request(endpoint, { method: 'DELETE' });
    }

    /**
     * Подготавливает тело запроса и заголовки в зависимости от метода HTTP и типа данных.
     *
     * @param {string} method - Метод HTTP (POST, PUT и т.д.).
     * @param {Object|FormData} data - Данные для отправки в теле запроса.
     * @param {string} entityType - Тип сущности ('json' или 'formdata').
     * @returns {Object} - Объект опций для Fetch API.
     */
    _prepareBody(method, data, entityType) {
        let options = {
            method: method,
            headers: { 'Accept': 'application/json' }
        };

        if (entityType === 'formdata') {
            options.body = data;
            // Удаляем заголовок 'Content-Type', если он был установлен ранее
            delete options.headers['Content-Type'];
        } else {
            options.body = JSON.stringify(data);
            options.headers['Content-Type'] = 'application/json';
        }
        return options;
    }
}

/**
 * Pagination
 *
 * Управляет элементами пагинации и взаимодействием с пользователем.
 */
class Pagination {
    constructor(containerId, onPageChange) {
        this.container = document.getElementById(containerId);
        this.onPageChange = onPageChange;
        this.currentPage = 0;
        this.totalPages = 0;

        this.container.addEventListener('click', this._handleClick.bind(this));
    }

    setup(totalPages, currentPage) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.render();
    }

    _handleClick(e) {
        const link = e.target.closest('a');
        if (link && this.container.contains(link)) {
            e.preventDefault();
            const page = link.getAttribute('data-page');
            if (page !== null) {
                const pageNumber = parseInt(page, 10);
                if (!isNaN(pageNumber) && pageNumber >= 0 && pageNumber < this.totalPages) {
                    this.onPageChange(pageNumber);
                } else {
                    console.error("Invalid page number:", page);
                }
            }
        }
    }

    render() {
        if (this.totalPages === 0) {
            this.container.innerHTML = '';
            return;
        }

        let paginationItems = '';
        paginationItems += this._createNavItem(0, '<i class="ti ti-chevrons-left ti-xs"></i>', 'first', this.currentPage > 0);
        paginationItems += this._createNavItem(this.currentPage - 1, '<i class="ti ti-chevron-left ti-xs"></i>', 'prev', this.currentPage > 0);
        paginationItems += this._renderPageNumbers();
        paginationItems += this._createNavItem(this.currentPage + 1, '<i class="ti ti-chevron-right ti-xs"></i>', 'next', this.currentPage < this.totalPages - 1);
        paginationItems += this._createNavItem(this.totalPages - 1, '<i class="ti ti-chevrons-right ti-xs"></i>', 'last', this.currentPage < this.totalPages - 1);

        this.container.innerHTML = paginationItems;
    }

    _renderPageNumbers() {
        const ellipsisThreshold = 2;
        const ellipsisOffset = 1;
        let paginationItems = '';

        const addFirstPageAndEllipsis = () => {
            paginationItems += this._createPageItem(0, '1');
            if (this.currentPage > ellipsisThreshold + ellipsisOffset) {
                paginationItems += this._createDisabledItem('...');
            }
        };

        const addLastPageAndEllipsis = () => {
            if (this.currentPage < this.totalPages - (ellipsisThreshold + ellipsisOffset + 1)) {
                paginationItems += this._createDisabledItem('...');
            }
            paginationItems += this._createPageItem(this.totalPages - 1, this.totalPages);
        };

        if (this.currentPage > ellipsisThreshold) {
            addFirstPageAndEllipsis();
        }

        const startPage = Math.max(0, this.currentPage - ellipsisOffset);
        const endPage = Math.min(this.totalPages - 1, this.currentPage + ellipsisOffset);

        for (let i = startPage; i <= endPage; i++) {
            paginationItems += this._createPageItem(i, i + 1, i === this.currentPage);
        }

        if (this.currentPage < this.totalPages - (ellipsisThreshold + 1)) {
            addLastPageAndEllipsis();
        }

        return paginationItems;
    }

    _createNavItem(page, content, className, isEnabled) {
        return `<li class="page-item ${className} ${isEnabled ? '' : 'disabled'}"> <a class="page-link" href="#" data-page="${page}">${content}</a> </li>`;
    }

    _createPageItem(page, text, isActive = false) {
        return `<li class="page-item ${isActive ? 'active' : ''}"> <a class="page-link" href="#" data-page="${page}">${text}</a> </li>`;
    }

    _createDisabledItem(text) {
        return `<li class="page-item disabled"> <span class="page-link">${text}</span> </li>`;
    }
}

/**
 * GenericTable
 *
 * Отвечает за отображение данных в таблице и обработку действий редактирования/удаления.
 */
class GenericTable {
    constructor({ tableBodyId, columns, onEdit, onDelete, noDataText = 'Нет данных для отображения' }) {
        this.tableBody = document.getElementById(tableBodyId);
        this.columns = columns;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        this.noDataText = noDataText;
    }

    populate(items) {
        this.tableBody.innerHTML = '';

        if (items.length === 0) {
            this._renderNoDataRow();
            return;
        }

        const fragment = document.createDocumentFragment();
        items.forEach(item => {
            const row = this._createRow(item);
            fragment.appendChild(row);
        });
        this.tableBody.appendChild(fragment);
    }

    _createActionButton(action, id, iconClass, className) {
        return `<button class="btn btn-sm ${className}" data-action="${action}" data-id="${id}"><i class="${iconClass}"></i></button>`;
    }

    _createRow(item) {
        const row = document.createElement('tr');

        let cells = '';
        this.columns.forEach(col => {
            let cellValue = '';
            if (typeof col.render === 'function') {
                cellValue = col.render(item);
            } else if (col.key) {
                cellValue = item[col.key] !== undefined ? item[col.key] : '';
            }
            cells += `<td class="text-center">${cellValue}</td>`;
        });

        const actionCell = `
            <td class="text-center">
                ${this._createActionButton('edit', item.id, 'ti ti-pencil', 'btn-warning me-2')}
                ${this._createActionButton('delete', item.id, 'ti ti-trash', 'btn-danger')}
            </td>
        `;

        row.innerHTML = cells + actionCell;

        row.querySelectorAll('button').forEach(button => {
            const action = button.getAttribute('data-action');
            const id = button.getAttribute('data-id');
            if (action === 'edit') {
                button.addEventListener('click', () => this.onEdit(id));
            } else if (action === 'delete') {
                button.addEventListener('click', () => this.onDelete(id));
            }
        });

        return row;
    }

    _renderNoDataRow() {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="${this.columns.length + 1}" class="text-center">${this.noDataText}</td>`;
        this.tableBody.appendChild(row);
    }
}

/**
 * GenericEntityModal
 *
 * Управляет модальным окном формы для создания и редактирования сущностей.
 */
class GenericEntityModal {
    constructor({ modalId, formId, fields, onSubmit }) {
        this.modal = document.getElementById(modalId);
        this.form = document.getElementById(formId);
        this.fieldsConfig = fields;
        this.onSubmit = onSubmit;
        this.isEditMode = false;
        this.editId = null;

        this._initializeFormElements();
        this._initializeMedia();
        this._attachEventListeners();
    }

    _initializeFormElements() {
        this.fields = {};
        this.fieldsConfig.forEach(field => {
            const input = this.form.querySelector(`[name="${field.name}"]`);
            if (input) {
                this.fields[field.name] = input;
            }
        });
    }

    _initializeMedia() {
        this.mediaFields = this.fieldsConfig.filter(field => field.type === 'file' || field.type === 'image');
        this.mediaPreviews = {};

        this.mediaFields.forEach(field => {
            const previewElement = document.getElementById(field.previewId);
            if (previewElement) {
                this.mediaPreviews[field.name] = {
                    element: previewElement,
                    defaultSrc: field.type === 'image' ? previewElement.getAttribute('src') || '/customAssets/img.jpg' : null,
                    resetButton: document.getElementById(field.resetButtonId),
                    fieldType: field.type
                };

                if (this.mediaPreviews[field.name].resetButton) {
                    this.mediaPreviews[field.name].resetButton.addEventListener('click', () => {
                        this.resetMedia(field.name);
                    });
                }

                const input = this.fields[field.name];
                if (input) {
                    input.addEventListener('change', () => this._handleFileChange(field.name));
                }
            }
        });
    }

    _handleFileChange(fieldName) {
        const fileInput = this.fields[fieldName];
        const previewData = this.mediaPreviews[fieldName];

        if (fileInput.files && fileInput.files[0]) {
            if (previewData.fieldType === 'image') {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewData.element.src = e.target.result;
                };
                reader.readAsDataURL(fileInput.files[0]);
            } else {
                console.log("Selected file:", fileInput.files[0].name);
            }
        }
    }

    resetMedia(fieldName) {
        const previewData = this.mediaPreviews[fieldName];
        if (previewData.fieldType === 'image') {
            previewData.element.src = previewData.defaultSrc;
        }
        this.fields[fieldName].value = '';
    }

    _attachEventListeners() {
        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSubmit();
        });

        this.form.addEventListener('reset', () => {
            this.clearForm();
        });
    }

    open(isEdit = false, entityData = null) {
        this.isEditMode = isEdit;

        this.mediaFields.forEach(field => {
            if (this.mediaPreviews[field.name]) {
                this.resetMedia(field.name);
            }
        });

        if (isEdit && entityData) {
            this.editId = entityData.id;
            this.fieldsConfig.forEach(field => {
                if (this.fields[field.name]) {
                    if (field.name === 'isActive' && entityData[field.name] !== undefined) {
                        this.fields[field.name].checked = entityData[field.name];
                    } else if (field.type === 'image' && entityData[field.iconKey]) {
                        this.mediaPreviews[field.name].element.src = entityData[field.iconKey];
                        this.mediaPreviews[field.name].element.onerror = () => {
                            this.mediaPreviews[field.name].element.src = this.mediaPreviews[field.name].defaultSrc;
                            this.mediaPreviews[field.name].element.onerror = null;
                        };
                    } else if (field.type === 'file' && entityData[field.fileKey]) {
                        console.log("File URL:", entityData[field.fileKey]);
                    } else if (field.type === 'select' && entityData[field.name]) {
                        // Установка выбранного значения для select
                        const selectElement = this.fields[field.name];
                        $(selectElement).val(entityData[field.name]).trigger('change');
                    } else {
                        this.fields[field.name].value = entityData[field.name] || '';
                    }
                }
            });
        } else {
            this.clearForm();
        }

        const modalTitle = this.modal.querySelector('.modal-title');
        modalTitle.textContent = isEdit ? 'Редактировать' : 'Создать';

        this.modalInstance.show();
    }

    clearForm() {
        this.form.reset();
        this.editId = null;
        this.isEditMode = false;
        this._removeValidationErrors();
    }

    _removeValidationErrors() {
        const errorMessages = this.form.querySelectorAll('.error-message');
        const errorFields = this.form.querySelectorAll('.is-invalid');
        errorMessages.forEach(msg => msg.remove());
        errorFields.forEach(field => field.classList.remove('is-invalid'));
    }

    async handleSubmit() {
        this._removeValidationErrors();

        let data = {};
        if (this.fieldsConfig.some(field => field.type === 'file' || field.type === 'image')) {
            data = new FormData(this.form);
            this.fieldsConfig.filter(field => field.type === 'checkbox').forEach(field => {
                data.append(field.name, this.fields[field.name].checked);
            });
        } else {
            this.fieldsConfig.forEach(field => {
                if (field.name === 'isActive') {
                    data[field.name] = this.fields[field.name].checked;
                } else {
                    console.log("Field name:", field.name);
                    data[field.name] = this.fields[field.name].value.trim();
                }
            });
        }

        if (this.isEditMode) {
            if (this.editId === null) {
                console.error("Missing ID in edit mode");
                return;
            }
            if (data instanceof FormData) {
                if(!data.get('password')) {
                    data.delete('password');
                }
            } else if (!data.password) {
                delete data.password;
            }
        } else {
            if (data instanceof FormData){
                data.delete('id');
            } else {
                delete data.id;
            }
        }

        try {
            await this.onSubmit(this.isEditMode, this.editId, data);
            this.close();
            $('.modal-backdrop').remove();
        } catch (error) {
            if (error.status === 400 && error.data) {
                this._displayValidationErrors(error.data);
            } else {
                this._handleError('Ошибка при сохранении:', error);
            }
        }
    }

    _displayValidationErrors(errors) {
        for (const field in errors) {
            if (errors.hasOwnProperty(field)) {
                const errorMessage = errors[field];
                const inputField = this.form.querySelector(`[name="${field}"]`);
                if (inputField) {
                    const errorSpan = document.createElement('span');
                    errorSpan.className = 'text-danger error-message';
                    errorSpan.textContent = errorMessage;
                    inputField.classList.add('is-invalid');
                    inputField.parentElement.appendChild(errorSpan);
                }
            }
        }
    }

    _handleError(message, error) {
        console.error(message, error);
        this.showToast('error', 'Ошибка', message + (error.data ? ' ' + (error.data.message || error.data.error || JSON.stringify(error.data)) : ''));
    }

    close() {
        this.modalInstance.hide();
    }

    initializeModal() {
        this.modalInstance = new bootstrap.Modal(this.modal, {
            backdrop: 'static',
            keyboard: false
        });
    }
}


/**
 * GenericDeleteConfirmModal
 *
 * Обрабатывает модальное окно подтверждения удаления.
 */
class GenericDeleteConfirmModal {
    constructor(modalId, confirmButtonId, onConfirm) {
        this.modal = document.getElementById(modalId);
        this.confirmButton = document.getElementById(confirmButtonId);
        this.onConfirm = onConfirm;
        this.entityId = null;

        this.confirmButton.addEventListener('click', this._handleConfirm.bind(this));
        this.initializeModal();
    }

    _handleConfirm() {
        if (this.entityId !== null) {
            this.onConfirm(this.entityId);
            this.close();
        }
    }

    open(id) {
        this.entityId = id;
        this.modalInstance.show();
    }

    close() {
        this.modalInstance.hide();
        this.entityId = null;
    }

    initializeModal() {
        this.modalInstance = new bootstrap.Modal(this.modal, {
            backdrop: 'static',
            keyboard: false
        });
    }
}

/**
 * GenericCRUDApp
 *
 * Координирует все компоненты для создания единого CRUD-приложения.
 */
class GenericCRUDApp {
    constructor(config) {
        this.apiService = new GenericAPIService(config.api);
        this.entityName = config.entityName || '';
        this.table = new GenericTable({
            tableBodyId: config.tableBodyId,
            columns: config.columns,
            onEdit: this.handleEdit.bind(this),
            onDelete: this.handleDelete.bind(this)
        });
        this.pagination = new Pagination(config.paginationId, this.handlePageChange.bind(this));
        this.modal = new GenericEntityModal({
            modalId: config.modalId,
            formId: config.formId,
            fields: config.formFields,
            onSubmit: this.handleFormSubmit.bind(this)
        });
        this.deleteConfirmModal = new GenericDeleteConfirmModal(
            config.deleteModalId,
            config.confirmDeleteBtnId,
            this.handleDeleteConfirm.bind(this)
        );

        this.searchInput = document.getElementById(config.searchInputId);
        this.pageSizeSelect = document.getElementById(config.pageSizeSelectId);
        this.createButton = document.getElementById(config.createButtonId);
        this.loadingIndicator = document.getElementById(config.loadingIndicatorId);

        this.currentPage = 0;
        this.pageSize = parseInt(this.pageSizeSelect.value, 10);
        this.searchQuery = '';

        this.toastConfig = config.toastConfig || {};
        this.notificationKeyword = config.notificationKeyword || 'notification';

        this._initialize();
    }

    _initialize() {
        this.modal.initializeModal();
        this.deleteConfirmModal.initializeModal();
        this.fetchAndRender();
        this._attachEventListeners();
    }

    _attachEventListeners() {
        this.pageSizeSelect.addEventListener('change', () => {
            this.pageSize = parseInt(this.pageSizeSelect.value, 10);
            this.currentPage = 0;
            this.fetchAndRender();
        });

        this.createButton.addEventListener('click', () => {
            this.modal.open(false);
        });

        let debounceTimeout;
        this.searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                this.searchQuery = this.searchInput.value.trim();
                this.currentPage = 0;
                this.fetchAndRender();
            }, 300);
        });
    }

    blockCard(message = 'Загрузка...') {
        $('#card-block').block({
            message: `
                <span class="spinner-border text-primary" role="status"></span>
                <span>${message}</span>
            `,
            css: {
                backgroundColor: 'transparent',
                border: '0'
            },
            overlayCSS: {
                opacity: 0.5
            }
        });
    }

    unblockCard() {
        $('#card-block').unblock();
    }

    async fetchAndRender() {
        try {
            this.blockCard();
            const data = await this.apiService.getAll(this.currentPage, this.pageSize, this.searchQuery);
            const items = data.content;

            if (this.currentPage >= data.totalPages && data.totalPages > 0) {
                this.currentPage = data.totalPages - 1;
                const updatedData = await this.apiService.getAll(this.currentPage, this.pageSize, this.searchQuery);
                this.table.populate(updatedData.content);
                this.pagination.setup(updatedData.totalPages, this.currentPage);
                return;
            }

            this.table.populate(items);
            this.pagination.setup(data.totalPages, this.currentPage);
        } catch (error) {
            this.showToast('error', 'Ошибка', 'Ошибка при загрузке данных:');
        } finally {
            this.unblockCard();
        }
    }

    handlePageChange(newPage) {
        this.currentPage = newPage;
        this.fetchAndRender();
    }

    handleEdit(id) {
        this.apiService.getById(id)
            .then(data => {
                this.modal.open(true, data);
            })
            .catch(error => {
                this.showToast('error', 'Ошибка', 'Ошибка при получении данных для редактирования:');
            });
    }

    handleDelete(id) {
        this.deleteConfirmModal.open(id);
    }

    async handleDeleteConfirm(id) {
        try {
            const result = await this.apiService.delete(id);
            const dataAfterDeletion = await this.apiService.getAll(this.currentPage, this.pageSize, this.searchQuery);
            if (dataAfterDeletion.content.length === 0 && this.currentPage > 0) {
                this.currentPage--;
            }
            this.fetchAndRender();
            if (result && result[this.notificationKeyword]) {
                this.showToast('success', 'Успех', result[this.notificationKeyword]);
            } else {
                this.showToast('success', 'Успех', this.entityName + ' успешно удален!');
            }
        } catch (error) {
            if (error.data && error.data[this.notificationKeyword]) {
                this.showToast('error', 'Ошибка', error.data[this.notificationKeyword]);
            } else {
                this.showToast('error', 'Ошибка', 'Ошибка при удалении:');
            }
        }
    }

    async handleFormSubmit(isEditMode, id, data) {
        try {
            let result;
            const entityType = data instanceof FormData ? 'formdata' : 'json';

            if (isEditMode) {
                result = await this.apiService.update(id, data, entityType);
            } else {
                result = await this.apiService.create(data, entityType);
            }
            this.fetchAndRender();
            if (result && result[this.notificationKeyword]) {
                this.showToast('success', 'Успех', result[this.notificationKeyword]);
            } else {
                this.showToast('success', 'Успех', this.entityName + (isEditMode ? ' успешно обновлен!' : ' успешно создан!'));
            }
        } catch (error) {
            if (error.data && error.data[this.notificationKeyword]) {
                this.showToast('error', 'Ошибка', error.data[this.notificationKeyword]);
            }
            throw error;
        }
    }

    showToast(type, title, message) {
        toastr.options = {
            closeButton: true,
            debug: false,
            newestOnTop: false,
            progressBar: false,
            positionClass: 'toast-top-right',
            preventDuplicates: false,
            onclick: null,
            showDuration: '300',
            hideDuration: '1000',
            timeOut: '5000',
            extendedTimeOut: '1000',
            showEasing: 'swing',
            hideEasing: 'linear',
            showMethod: 'fadeIn',
            hideMethod: 'fadeOut',
            ...this.toastConfig
        };

        toastr[type](message, title);
    }
}
