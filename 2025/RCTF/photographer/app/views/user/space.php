<?php
$pageTitle = 'My Space';
$pageCSS = ['pages/space', 'components/photo-viewer'];
$pageJS = ['components/photo-viewer', 'pages/space'];
$navType = 'space';
include __DIR__ . '/../layouts/header.php';
$user = Auth::user();
?>

<div class="space-container">
    
    <div class="space-wrapper">
        <div class="profile-section">
            <div class="profile-header" onclick="openBackgroundPicker()" 
                 style="<?= !empty($user['saved_filename']) ? 'background-image: url(/uploads/photos/' . e($user['saved_filename']) . '); background-size: cover; background-position: center;' : '' ?>">
                <div class="change-bg-hint">
                    <i class="fas fa-camera"></i> Change Background
                </div>
            </div>
            
            <div class="profile-info-section">
                <div class="profile-avatar-wrapper" onclick="openAvatarPicker()">
                    <img src="<?= e($user['avatar_url']) ?>" alt="Avatar" class="profile-avatar">
                    <div class="change-avatar-hint">
                        <i class="fas fa-camera"></i>
                    </div>
                </div>
                
                <div class="profile-details">
                    <div class="profile-username">
                        <?= e($user['username']) ?>
                        <span class="user-level">
                            Lv.<?= $user['level'] ?> <?= getUserLevelTitle($user['level']) ?>
                        </span>
                    </div>
                    <?php if (!empty($user['bio'])): ?>
                        <p class="profile-bio"><?= e($user['bio']) ?></p>
                    <?php endif; ?>
                </div>
            </div>
        </div>
        
        <div class="tabs-container">
            <div class="tabs">
                <button class="tab-btn active" data-tab="posts">
                    <i class="fas fa-file-alt"></i>
                    <span>Blog</span>
                </button>
                <button class="tab-btn" data-tab="photos">
                    <i class="fas fa-images"></i>
                    <span>Gallery</span>
                </button>
            </div>
        </div>
        
        <div class="content-container">
            <div id="posts-content" class="tab-content active">
                <div class="posts-list">
                    <div class="loading-state">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>Loading...</p>
                    </div>
                </div>
            </div>
            
            <div id="photos-content" class="tab-content">
                <div class="photos-grid">
                    <div class="loading-state">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>Loading...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<a href="/compose" class="fab-button" title="Publish Work">
    <i class="fas fa-pen"></i>
</a>

<input type="file" id="avatarUpload" accept="image/*" style="display: none;">

<div id="backgroundModal" class="modal">
    <div class="modal-content modal-large">
        <div class="modal-header">
            <h2>Select Background</h2>
            <button class="modal-close" onclick="closeBackgroundModal()">&times;</button>
        </div>
        <div id="photoGallery" class="photo-gallery-modal">
            <div class="loading-state">
                <i class="fas fa-spinner fa-spin"></i>
                <p>Loading...</p>
            </div>
        </div>
    </div>
</div>

<script>
const csrfToken = '<?= e(Auth::generateCSRFToken()) ?>';
const currentUserId = '<?= Auth::id() ?>';

function openAvatarPicker() {
    document.getElementById('avatarUpload').click();
}

document.getElementById('avatarUpload').addEventListener('change', async function(e) {
    const file = e.target.files[0];
    if (!file) return;
    
    const formData = new FormData();
    formData.append('avatar', file);
    formData.append('csrf_token', csrfToken);
    
    try {
        const response = await fetch('/api/user/avatar', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('Upload failed');
    }
});

async function openBackgroundPicker() {
    try {
        const response = await fetch('/api/user/photos');
        const data = await response.json();
        
        if (data.success) {
            const gallery = document.getElementById('photoGallery');
            
            if (data.photos.length === 0) {
                gallery.innerHTML = `
                    <div class="empty-state">
                        <i class="fas fa-images"></i>
                        <p>No photos uploaded yet</p>
                        <p class="help-text">Publish works first, then you can select photos as background</p>
                    </div>
                `;
            } else {
                gallery.innerHTML = `
                    <div class="bg-remove-option" onclick="removeBackground()">
                        <i class="fas fa-times"></i>
                        <span>Remove Background</span>
                    </div>
                    ${data.photos.map(photo => `
                        <div class="gallery-item" onclick="selectBackground('${photo.id}')">
                            <img src="/uploads/photos/${photo.saved_filename}" alt="${photo.original_filename}">
                            <div class="gallery-item-overlay">
                                <i class="fas fa-check"></i>
                            </div>
                        </div>
                    `).join('')}
                `;
            }
            
            document.getElementById('backgroundModal').style.display = 'block';
        }
    } catch (error) {
        alert('Failed to load');
    }
}

function closeBackgroundModal() {
    document.getElementById('backgroundModal').style.display = 'none';
}

async function selectBackground(photoId) {
    const formData = new FormData();
    formData.append('photo_id', photoId);
    formData.append('csrf_token', csrfToken);
    
    try {
        const response = await fetch('/api/user/background', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            closeBackgroundModal();
            location.reload();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('Setting failed');
    }
}

async function removeBackground() {
    if (!confirm('Are you sure you want to remove the background?')) return;
    
    closeBackgroundModal();
    
    const formData = new FormData();
    formData.append('photo_id', '');
    formData.append('csrf_token', csrfToken);
    
    try {
        const response = await fetch('/api/user/background', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            closeBackgroundModal();
            location.reload();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('Operation failed');
    }
}

window.onclick = function(event) {
    const backgroundModal = document.getElementById('backgroundModal');
    if (event.target === backgroundModal) {
        closeBackgroundModal();
    }
}
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>
