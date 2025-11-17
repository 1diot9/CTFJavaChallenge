// Article audit page functions

async function approveArticle(id) {
    await updateArticleStatus(id, 'approve', 'Article approved successfully');
}

async function rejectArticle(id) {
    await updateArticleStatus(id, 'reject', 'Article rejected');
}

async function updateArticleStatus(id, action, successMsg) {
    try {
        const response = await fetch(`/articles/${action}/${id}`, {
            method: 'POST',
            headers: CsrfHelper.getHeaders()
        });
        const result = await response.json();

        if (result.success) {
            showNotification(successMsg, 'success');
            // Redirect to article page after successful audit
            setTimeout(() => {
                window.location.href = `/article/${id}`;
            }, 1500);
        } else {
            showNotification(result.message, 'error');
        }
    } catch (error) {
        console.error('Update failed:', error);
        showNotification('Operation failed, please try again', 'error');
    }
}

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 16px 24px;
        background: ${type === 'success' ? '#28a745' : '#dc3545'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add animation styles
(function() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOut {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(400px);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
})();

