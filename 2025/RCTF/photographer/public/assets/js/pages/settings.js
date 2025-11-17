document.addEventListener('DOMContentLoaded', function() {
    initSettingsPage();
});

function initSettingsPage() {
    const hash = window.location.hash.substring(1);
    if (hash) {
        const navItem = document.querySelector(`[href="#${hash}"]`);
        if (navItem) {
            navItem.click();
        }
    }
    
    const avatarUpload = document.getElementById('avatarUpload');
    if (avatarUpload) {
        avatarUpload.addEventListener('change', handleAvatarUpload);
    }
    
    const usernameForm = document.getElementById('usernameForm');
    if (usernameForm) {
        usernameForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await submitForm(this, '/api/user/username', 'Username updated successfully');
        });
    }
    
    const bioForm = document.getElementById('bioForm');
    if (bioForm) {
        bioForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await submitForm(this, '/api/user/bio', 'Bio updated successfully');
        });
    }
    
    const passwordForm = document.getElementById('passwordForm');
    if (passwordForm) {
        passwordForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const result = await submitForm(this, '/api/user/password', 'Password changed successfully');
            if (result) {
                this.reset();
            }
        });
    }
}

function showSection(sectionName) {
    document.querySelectorAll('.settings-nav-item').forEach(item => {
        item.classList.remove('active');
    });
    event.target.closest('.settings-nav-item').classList.add('active');
    
    document.querySelectorAll('.settings-content-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById('section-' + sectionName).classList.add('active');
    
    window.location.hash = sectionName;
}

async function handleAvatarUpload(e) {
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
            const avatarImg = document.getElementById('currentAvatar');
            if (avatarImg) {
                avatarImg.src = data.avatar_url + '?' + Date.now();
            }
            showNotification('Avatar updated successfully');
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Upload failed');
    }
}

async function openBackgroundSelector() {
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
                const removeOption = `
                    <div class="bg-remove-option" onclick="removeBackground()">
                        <i class="fas fa-times-circle"></i>
                        <span>Remove Background</span>
                    </div>
                `;
                const photosHTML = data.photos.map(photo => `
                    <div class="gallery-item" onclick="selectBackground('${photo.id}')">
                        <img src="/uploads/photos/${photo.saved_filename}" alt="${escapeHtml(photo.original_filename)}">
                    </div>
                `).join('');
                
                gallery.innerHTML = removeOption + photosHTML;
            }
            
            document.getElementById('backgroundModal').style.display = 'block';
        }
    } catch (error) {
        showNotification('Failed to load');
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
            showNotification('Background set successfully');
            closeBackgroundModal();
            setTimeout(() => {
                window.location.reload();
            }, 500);
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Setting failed');
    }
}

async function removeBackground() {
    if (!confirm('Are you sure you want to remove the background?')) return;
    
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
            showNotification('Background removed');
            closeBackgroundModal();
            setTimeout(() => {
                window.location.reload();
            }, 500);
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Operation failed');
    }
}

window.onclick = function(event) {
    const modal = document.getElementById('backgroundModal');
    if (event.target === modal) {
        closeBackgroundModal();
    }
}
