let uploadedPhotos = [];
let currentTab = 'posts';

document.addEventListener('DOMContentLoaded', function() {
    loadPosts();
    initTabs();
});

function initTabs() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    
    tabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tab = this.dataset.tab;
            switchTab(tab);
        });
    });
}

function switchTab(tab) {
    currentTab = tab;
    
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.tab === tab) {
            btn.classList.add('active');
        }
    });
    
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(tab + '-content').classList.add('active');
    
    if (tab === 'posts') {
        loadPosts();
    } else if (tab === 'photos') {
        loadPhotos();
    }
}

async function loadPosts() {
    try {
        const response = await fetch('/space/posts');
        const data = await response.json();
        
        if (data.success) {
            displayPosts(data.posts);
        }
    } catch (error) {
        console.error('Failed to load posts', error);
    }
}

function displayPosts(posts) {
    const container = document.querySelector('.posts-list');
    
    if (posts.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-camera"></i>
                <p>No works published yet</p>
                <p class="help-text">Share your photography works and capture beautiful moments</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = posts.map(post => `
        <div class="post-card" onclick="goToPost('${post.id}', event)">
            <div class="post-header">
                <h3 class="post-title">${escapeHtml(post.title)}</h3>
                <div class="post-meta">
                    <span class="post-time">${post.created_at}</span>
                    <button class="btn-delete" onclick="deletePost('${post.id}', event)">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            
            ${post.content ? `<p class="post-content">${escapeHtml(post.content).substring(0, 100)}${post.content.length > 100 ? '...' : ''}</p>` : ''}
            
            ${post.photo_list.length > 0 ? `
                <div class="post-photos ${post.photo_list.length === 1 ? 'single-photo' : ''}">
                    ${post.photo_list.slice(0, 4).map(photo => `
                        <img src="/uploads/photos/${photo.saved_filename}" 
                             alt="Photo"
                             class="post-photo">
                    `).join('')}
                    ${post.photo_list.length > 4 ? `<div class="more-photos">+${post.photo_list.length - 4}</div>` : ''}
                </div>
            ` : ''}
        </div>
    `).join('');
}

async function loadPhotos() {
    try {
        const response = await fetch('/space/photos');
        const data = await response.json();
        
        if (data.success) {
            displayPhotos(data.photos);
        }
    } catch (error) {
        console.error('Failed to load photos', error);
    }
}

function displayPhotos(photos) {
    const container = document.querySelector('.photos-grid');
    
    if (photos.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-images"></i>
                <p>No photos uploaded yet</p>
                <p class="help-text">Photos uploaded when publishing works will appear here</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = photos.map(photo => `
        <div class="gallery-item" onclick="viewPhoto('${photo.id}')">
            <img src="/uploads/photos/${photo.saved_filename}" alt="${escapeHtml(photo.original_filename)}">
            <div class="gallery-item-overlay">
                <i class="fas fa-search-plus"></i>
            </div>
        </div>
    `).join('');
}

function viewPhoto(photoId) {
    if (typeof photoViewer !== 'undefined') {
        photoViewer.open(photoId, {
            showDelete: true,
            currentUserId: currentUserId,
            onDelete: function() {
                loadPhotos();
            }
        });
    }
}

function goToPost(postId, event) {
    window.location.href = `/post/${postId}`;
}

async function deletePost(postId, event) {
    event.stopPropagation();
    if (!confirm('Are you sure you want to delete this post?')) return;
    
    const formData = new FormData();
    formData.append('post_id', postId);
    formData.append('csrf_token', csrfToken);
    
    try {
        const response = await fetch('/api/posts/delete', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            loadPosts();
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Failed to delete');
    }
}

window.onclick = function(event) {
    const backgroundModal = document.getElementById('backgroundModal');
    
    if (event.target === backgroundModal) {
        closeBackgroundModal();
    }
}
