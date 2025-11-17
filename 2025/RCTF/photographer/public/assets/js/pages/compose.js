let uploadedPhotos = [];
let hasUnsavedChanges = false;

document.addEventListener('DOMContentLoaded', function() {
    initComposePage();
});

function initComposePage() {
    const titleInput = document.getElementById('postTitle');
    const contentInput = document.getElementById('postContent');
    const publishBtn = document.getElementById('publishBtn');
    const photoUpload = document.getElementById('photoUpload');
    
    if (titleInput) {
        titleInput.addEventListener('input', function() {
            updatePublishButton();
            hasUnsavedChanges = true;
        });
    }
    
    if (contentInput) {
        contentInput.addEventListener('input', function() {
            hasUnsavedChanges = true;
        });
    }
    
    if (photoUpload) {
        photoUpload.addEventListener('change', handlePhotoUpload);
    }
    
    if (publishBtn) {
        publishBtn.addEventListener('click', handlePublish);
    }
    
    window.addEventListener('beforeunload', function(e) {
        if (hasUnsavedChanges) {
            e.preventDefault();
            e.returnValue = '';
        }
    });
}

async function handlePhotoUpload(e) {
    const files = e.target.files;
    if (files.length === 0) return;
    
    const uploadBtn = document.querySelector('.toolbar-btn');
    uploadBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    uploadBtn.disabled = true;
    
    const formData = new FormData();
    for (let file of files) {
        formData.append('photos[]', file);
    }
    
    try {
        const response = await fetch('/api/photos/upload', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            uploadedPhotos = uploadedPhotos.concat(data.photos);
            updatePhotoPreview();
            updatePublishButton();
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Upload failed');
    } finally {
        uploadBtn.innerHTML = '<i class="fas fa-image"></i>';
        uploadBtn.disabled = false;
        e.target.value = '';
    }
}

function updatePhotoPreview() {
    const preview = document.getElementById('photoPreviewArea');
    
    if (uploadedPhotos.length === 0) {
        preview.innerHTML = '';
        preview.style.display = 'none';
    } else {
        preview.style.display = 'grid';
        preview.innerHTML = uploadedPhotos.map((photo, index) => `
            <div class="preview-photo-item">
                <img src="${photo.url}" alt="${photo.original_filename}">
                <button type="button" class="preview-photo-remove" onclick="removePhoto(${index})">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `).join('');
    }
    
    document.getElementById('photoIds').value = JSON.stringify(uploadedPhotos.map(p => p.id));
    document.getElementById('photoCount').textContent = `${uploadedPhotos.length} photos`;
}

function removePhoto(index) {
    uploadedPhotos.splice(index, 1);
    updatePhotoPreview();
    updatePublishButton();
}

function updatePublishButton() {
    const title = document.getElementById('postTitle').value.trim();
    const btn = document.getElementById('publishBtn');
    if (btn) {
        btn.disabled = !title;
    }
}

async function handlePublish() {
    const form = document.getElementById('composeForm');
    const formData = new FormData(form);
    const btn = document.getElementById('publishBtn');
    
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Publishing...';
    
    try {
        const response = await fetch('/api/posts/create', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            hasUnsavedChanges = false;
            window.location.href = '/post/' + data.post_id;
        } else {
            showNotification(data.message);
            btn.disabled = false;
            btn.textContent = 'Publish';
        }
    } catch (error) {
        showNotification('Publish failed');
        btn.disabled = false;
        btn.textContent = 'Publish';
    }
}

