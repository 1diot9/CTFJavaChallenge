class PhotoViewer {
    constructor() {
        this.currentPhoto = null;
        this.init();
    }
    
    init() {
        this.createModal();
        this.bindEvents();
    }
    
    createModal() {
        const modal = document.createElement('div');
        modal.id = 'photoViewerModal';
        modal.className = 'photo-modal';
        modal.innerHTML = `
            <div class="photo-viewer-container">
                <div class="photo-viewer-image">
                    <img id="photoViewerImage" src="" alt="">
                </div>
                <div class="photo-viewer-sidebar">
                    <div class="photo-viewer-header">
                        <h3 id="photoViewerTitle">Photo Information</h3>
                        <button class="photo-viewer-close" onclick="photoViewer.close()">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="photo-viewer-content" id="photoViewerContent">
                        <div class="loading-state">
                            <i class="fas fa-spinner fa-spin"></i>
                            <p>Loading...</p>
                        </div>
                    </div>
                    <div class="photo-viewer-actions" id="photoViewerActions" style="display: none;">
                        <button class="photo-action-btn danger" onclick="photoViewer.deletePhoto()">
                            <i class="fas fa-trash"></i> Delete Photo
                        </button>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }
    
    bindEvents() {
        document.getElementById('photoViewerModal').addEventListener('click', (e) => {
            if (e.target.id === 'photoViewerModal') {
                this.close();
            }
        });
        
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen()) {
                this.close();
            }
        });
    }
    
    async open(photoId, options = {}) {
        try {
            document.getElementById('photoViewerModal').classList.add('active');
            document.body.style.overflow = 'hidden';
            
            this.options = options;
            
            const response = await fetch(`/api/photos/${photoId}/info`);
            const data = await response.json();
            
            if (data.success) {
                this.currentPhoto = data.photo;
                this.render();
                
                const actionsDiv = document.getElementById('photoViewerActions');
                if (options.showDelete && data.photo.user_id === options.currentUserId) {
                    actionsDiv.style.display = 'flex';
                } else {
                    actionsDiv.style.display = 'none';
                }
            } else {
                showNotification('Failed to load photo information');
                this.close();
            }
        } catch (error) {
            console.error('Failed to load photo', error);
            showNotification('Failed to load photo information');
            this.close();
        }
    }
    
    render() {
        const photo = this.currentPhoto;
        
        document.getElementById('photoViewerImage').src = `/uploads/photos/${photo.saved_filename}`;
        document.getElementById('photoViewerTitle').textContent = photo.original_filename;
        
        let html = '';
        
        html += this.renderBasicInfo(photo);
        
        if (this.hasExifData(photo)) {
            html += this.renderExifInfo(photo);
        }
        
        document.getElementById('photoViewerContent').innerHTML = html;
    }
    
    renderBasicInfo(photo) {
        return `
            <div class="photo-info-section">
                <h4><i class="fas fa-info-circle"></i> Basic Information</h4>
                <div class="photo-info-item">
                    <span class="photo-info-label">Filename</span>
                    <span class="photo-info-value">${escapeHtml(photo.original_filename)}</span>
                </div>
                <div class="photo-info-item">
                    <span class="photo-info-label">Dimensions</span>
                    <span class="photo-info-value">${photo.width} × ${photo.height}</span>
                </div>
                <div class="photo-info-item">
                    <span class="photo-info-label">Size</span>
                    <span class="photo-info-value">${formatFileSize(photo.size)}</span>
                </div>
                <div class="photo-info-item">
                    <span class="photo-info-label">Upload Time</span>
                    <span class="photo-info-value">${photo.created_at}</span>
                </div>
            </div>
        `;
    }
    
    renderExifInfo(photo) {
        let html = `
            <div class="photo-info-section">
                <h4><i class="fas fa-camera"></i> Shooting Information</h4>
                <div class="exif-grid">
        `;
        
        if (photo.exif_make || photo.exif_model) {
            const camera = [photo.exif_make, photo.exif_model].filter(Boolean).join(' ');
            html += this.renderExifItem('camera', 'Camera', camera);
        }
        
        if (photo.exif_focal_length) {
            html += this.renderExifItem('ruler', 'Focal Length', `${photo.exif_focal_length}mm`);
        }
        
        if (photo.exif_f_number) {
            html += this.renderExifItem('circle', 'Aperture', `f/${photo.exif_f_number}`);
        }
        
        if (photo.exif_exposure_time) {
            html += this.renderExifItem('clock', 'Shutter Speed', `${photo.exif_exposure_time}s`);
        }
        
        if (photo.exif_iso) {
            html += this.renderExifItem('sun', 'ISO', photo.exif_iso);
        }
        
        if (photo.exif_date_taken) {
            html += this.renderExifItem('calendar', 'Date Taken', photo.exif_date_taken, true);
        }
        
        html += `
                </div>
            </div>
        `;
        
        if (photo.exif_software || photo.exif_artist || photo.exif_copyright) {
            html += '<div class="photo-info-section"><h4><i class="fas fa-tags"></i> Other Information</h4>';
            
            if (photo.exif_software) {
                html += `
                    <div class="photo-info-item">
                        <span class="photo-info-label">Software</span>
                        <span class="photo-info-value">${escapeHtml(photo.exif_software)}</span>
                    </div>
                `;
            }
            
            if (photo.exif_artist) {
                html += `
                    <div class="photo-info-item">
                        <span class="photo-info-label">Artist</span>
                        <span class="photo-info-value">${escapeHtml(photo.exif_artist)}</span>
                    </div>
                `;
            }
            
            if (photo.exif_copyright) {
                html += `
                    <div class="photo-info-item">
                        <span class="photo-info-label">Copyright</span>
                        <span class="photo-info-value">${escapeHtml(photo.exif_copyright)}</span>
                    </div>
                `;
            }
            
            html += '</div>';
        }
        
        return html;
    }
    
    renderExifItem(icon, label, value, fullWidth = false) {
        return `
            <div class="exif-item ${fullWidth ? 'exif-item-full' : ''}">
                <div class="exif-label">
                    <i class="fas fa-${icon}"></i>
                    ${label}
                </div>
                <div class="exif-value">${escapeHtml(value)}</div>
            </div>
        `;
    }
    
    hasExifData(photo) {
        return photo.exif_make || photo.exif_model || photo.exif_focal_length || 
               photo.exif_f_number || photo.exif_exposure_time || photo.exif_iso || 
               photo.exif_date_taken;
    }
    
    async deletePhoto() {
        if (!this.currentPhoto) return;
        
        if (!confirm('Are you sure you want to delete this photo?')) return;
        
        try {
            const formData = new FormData();
            formData.append('photo_id', this.currentPhoto.id);
            formData.append('csrf_token', csrfToken);
            
            const response = await fetch('/api/photos/delete', {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.success) {
                showNotification('Deleted successfully');
                
                const onDeleteCallback = this.options.onDelete;
                
                this.close();
                
                if (onDeleteCallback) {
                    onDeleteCallback();
                }
            } else {
                showNotification(data.message);
            }
        } catch (error) {
            console.error('Failed to delete', error);
            showNotification('Failed to delete');
        }
    }
    
    close() {
        document.getElementById('photoViewerModal').classList.remove('active');
        document.body.style.overflow = '';
        this.currentPhoto = null;
        this.options = {};
    }
    
    isOpen() {
        return document.getElementById('photoViewerModal').classList.contains('active');
    }
}

let photoViewer;
document.addEventListener('DOMContentLoaded', function() {
    photoViewer = new PhotoViewer();
});

function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}
